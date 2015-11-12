package org.spideruci.tacoco;

import org.apache.maven.cli.MavenCli;
import org.spideruci.tacoco.AbstractBuildProbe.Child;
import org.spideruci.tacoco.db.CreateSQLiteDB;
import org.spideruci.tacoco.util.PathBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.spideruci.tacoco.cli.AbstractCli.*;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

public class TacocoLauncher {

	private String tacocoHome, targetDir;
	private static String tacocoClasspath = null;
	private static String USER_DIR = System.getProperty("user.dir");

	private TacocoLauncher(String tacocoHome, String targetDir){
		this.tacocoHome = tacocoHome;
		this.targetDir = targetDir;
	}

	public static void main(String[] args) throws Exception{

		if(System.getProperties().containsKey(HELP)) {
			LANUCHER_CLI.printHelp();
		}

		System.setProperty("maven.multiModuleProjectDirectory", USER_DIR);

		String targetDir = readArgumentValue(SUT);
		if(targetDir.endsWith(File.separator)) targetDir = targetDir.substring(0, targetDir.length());
		TacocoLauncher launcher = new TacocoLauncher(readOptionalArgumentValue(HOME, USER_DIR)
				,targetDir);
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.targetDir);
		String name = readOptionalArgumentValue(PROJECT, probe.getId());

		launcher.setTacocoEnv();
		String parentCP = probe.getClasspath() + File.pathSeparator + launcher.getTacocoClasspath();

		if(probe.hasChild()){	
			for(Child child : probe.getChildren()){
				launcher.startJUnitRunner(name+"."+child.id, child.classpath + File.pathSeparator + parentCP, child.targetDir, child.jvmArgs);
			}
		}
		launcher.startJUnitRunner(name, parentCP, launcher.targetDir, null);
	}


	private void startJUnitRunner(String id, String classpath, String targetDir, String[] jvmArgs) {

		final String defaultOutputPath = 
				new PathBuilder().path(tacocoHome).path("tacoco_output").buildFilePath();
		String outdir = readOptionalArgumentValue(OUTDIR, defaultOutputPath);

		if(!new File(outdir).exists()) new File(outdir).mkdirs();

		//delete files before execution, 
		File exec = new File(outdir, id+".exec");
		File err = new File(outdir, id+".err");
		File log = new File(outdir, id+".log");
		if(exec.exists()) exec.delete();
		if(err.exists()) err.delete();
		if(log.exists()) log.delete();

		final String jacocoRuntimeLibPath = 
				new PathBuilder()
				.path(tacocoHome)
				.path("lib")
				.path("org.jacoco.agent-0.7.4.201502262128-runtime.jar")
				.buildFilePath();

		final String instrumenterLocation = readOptionalArgumentValue(INST, jacocoRuntimeLibPath);
		final String instrumentedArgs = readOptionalArgumentValue(INST_ARGS, 
				"destfile=" + outdir + File.separator + id + ".exec" + ",dumponexit=false");

		InstrumenterConfig jacocoConfig = InstrumenterConfig.get(instrumenterLocation, instrumentedArgs);
		ProcessBuilder builder = new ProcessBuilder(
				"java",
				"-cp", classpath,
				jacocoConfig.getMemory(),
				jacocoConfig.buildJavaagentOpt(),
				"-Dtacoco.sut="+targetDir,
				"-Dtacoco.output="+outdir,
				"-Dtacoco.log=off",
				"-Dtacoco.thread="+1,
				"org.spideruci.tacoco.JUnitRunner");
		builder.directory(new File(targetDir));
		builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		builder.redirectError(err);
		builder.redirectOutput(log);

		final Process p;
		try{
			p= builder.start();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					p.destroy();
				}
			}); 
			p.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
		String dbFile = outdir + File.separator + id + ".db";
		if(System.getProperties().containsKey(DB))
			try {
				CreateSQLiteDB.dump(dbFile, targetDir, exec.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private String getTacocoClasspath() throws Exception{

		if(tacocoClasspath != null) return tacocoClasspath;

		final String tacocoCpPath = new PathBuilder().path(tacocoHome).path("cp.txt").buildFilePath();
		if(!new File(tacocoCpPath).exists()) {
			MavenCli mavenCli = new MavenCli();
			mavenCli.doMain(new String[] {"dependency:build-classpath", "-Dmdep.outputFile=cp.txt"}, tacocoHome,
					System.out, System.out);
		}

		final String cpDependencies = new String(Files.readAllBytes(Paths.get("cp.txt")));
		final String tacocoTargetPath = new PathBuilder().path(tacocoHome).path("target").path("classes").buildFilePath();

		tacocoClasspath = new PathBuilder().path(cpDependencies).path(tacocoTargetPath).buildClassPath();
		return tacocoClasspath;
	}

	/*
	 * Move jacoco.jar from mvn repo to tacocoHome/lib
	 */
	private void setTacocoEnv() {
		final String dependencyLibPath = new PathBuilder().path(tacocoHome).path("lib").buildFilePath();
		if(new File(dependencyLibPath).exists()) return;
		MavenCli mavenCli = new MavenCli();
		mavenCli.doMain(new String[]{"dependency:copy-dependencies", "-DoutputDirectory=lib"}, tacocoHome, System.out, System.out);
	}

}
