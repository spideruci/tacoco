package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.AbstractCli.DB;
import static org.spideruci.tacoco.cli.AbstractCli.HELP;
import static org.spideruci.tacoco.cli.AbstractCli.HOME;
import static org.spideruci.tacoco.cli.AbstractCli.OUTDIR;
import static org.spideruci.tacoco.cli.AbstractCli.PROJECT;
import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;
import static org.spideruci.tacoco.cli.AbstractCli.LANUCHER_CLI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.spideruci.tacoco.AbstractBuildProbe.Child;
import org.spideruci.tacoco.db.CreateSQLiteDB;

public class TacocoLauncher {

	private String tacocoHome, targetDir;
	private static String tacocoClasspath = null;

	private TacocoLauncher(String tacocoHome, String targetDir){
		this.tacocoHome = tacocoHome;
		this.targetDir = targetDir;
	}

	public static void main(String[] args) throws Exception{

		if(System.getProperties().containsKey(HELP)) {
			LANUCHER_CLI.printHelp();
		}

		String targetDir = readArgumentValue(SUT);
		if(targetDir.endsWith("/")) targetDir = targetDir.substring(0, targetDir.length());
		TacocoLauncher launcher = new TacocoLauncher(readOptionalArgumentValue(HOME,System.getProperty("user.dir"))
				,targetDir);
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.targetDir);
		String name = readOptionalArgumentValue(PROJECT, probe.getId());

		launcher.setTacocoEnv();
		String parentCP = probe.getClasspath() +":"+ launcher.getTacocoClasspath(); 

		if(probe.hasChild()){	
			for(Child child : probe.getChildren()){
				launcher.startJUnitRunner(name+"."+child.id, child.classpath+":"+ parentCP, child.targetDir, child.jvmArgs);
			}
		}
		launcher.startJUnitRunner(name, parentCP, launcher.targetDir, null);
	}


	private void startJUnitRunner(String id, String classpath, String targetDir, String[] jvmArgs) {

		String outdir = readOptionalArgumentValue(OUTDIR, tacocoHome+"/tacoco_output");

		if(!new File(outdir).exists()) new File(outdir).mkdirs();

		//delete files before execution, 
		File exec = new File(outdir, id+".exec");
		File err = new File(outdir, id+".err");
		File log = new File(outdir, id+".log");
		if(exec.exists()) exec.delete();
		if(err.exists()) err.delete();
		if(log.exists()) log.delete();


		ProcessBuilder builder = new ProcessBuilder(
				"java",
				"-cp", classpath,
				"-Xmx1536M",// "-Duser.language=hi", "-Duser.country=IN",
				"-javaagent:"+tacocoHome+"/lib/org.jacoco.agent-0.7.4.201502262128-runtime.jar=destfile="
				+outdir+"/"+id+".exec"+",dumponexit=false",
				"-Dtacoco.sut="+targetDir,
				"-Dtacoco.output="+outdir,
				"-Dtacoco.log=off",
				"-Dtacoco.thread="+1,
				"org.spideruci.tacoco.JUnitRunner");//.inheritIO();
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
		String dbFile = outdir+"/"+id+".db";
		if(System.getProperties().containsKey(DB))
			try {
				CreateSQLiteDB.dump(dbFile, targetDir, exec.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private String getTacocoClasspath() throws Exception{

		if(tacocoClasspath != null) return tacocoClasspath;
		if(!new File(tacocoHome+"/cp.txt").exists()) {
			ProcessBuilder builder = new ProcessBuilder(
					"/usr/bin/mvn","dependency:build-classpath","-Dmdep.outputFile=cp.txt").inheritIO();
			builder.directory(new File(tacocoHome));
			Process p = builder.start();
			p.waitFor();
		}
		tacocoClasspath = new String(Files.readAllBytes(Paths.get("cp.txt")))+":"+ tacocoHome + "/target/classes";
		return tacocoClasspath;
	}

	/*
	 * Move jacoco.jar from mvn repo to tacocoHome/lib
	 */
	private void setTacocoEnv() {
		if(new File(tacocoHome+"/lib").exists()) return;
		ProcessBuilder builder = new ProcessBuilder("/usr/bin/mvn","dependency:copy-dependencies","-DoutputDirectory=lib").inheritIO();
		builder.directory(new File(tacocoHome));
		try{
			Process p = builder.start();
			p.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
