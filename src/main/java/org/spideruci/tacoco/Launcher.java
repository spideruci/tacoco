package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.AbstractCli.HELP;
import static org.spideruci.tacoco.cli.AbstractCli.HOME;
import static org.spideruci.tacoco.cli.AbstractCli.OUTDIR;
import static org.spideruci.tacoco.cli.AbstractCli.PROJECT;
import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.AbstractCli.LOG;
import static org.spideruci.tacoco.cli.AbstractCli.arg;
import static org.spideruci.tacoco.cli.AbstractCli.argEquals;
import static org.spideruci.tacoco.cli.AbstractCli.readBooleanArgument;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;
import static org.spideruci.tacoco.cli.AbstractCli.LANUCHER_CLI;
import static org.spideruci.tacoco.cli.AbstractCli.ANALYZER_OPTS;

import org.apache.maven.cli.MavenCli;
import org.spideruci.tacoco.util.PathBuilder;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.spideruci.tacoco.probe.AbstractBuildProbe;


public class Launcher {

	private final String tacocoHome;
	private final String sutHome;
	
	private Launcher(String tacocoHome, String sutHome){
		this.tacocoHome = tacocoHome;
		this.sutHome = sutHome;
	}

	public static void main(String[] args) throws Exception{

		if(System.getProperties().containsKey(HELP)) {
			LANUCHER_CLI.printHelp();
		}
		
		final String userDir = System.getProperty("user.dir");

		System.setProperty("maven.multiModuleProjectDirectory", userDir); // how does this help us?

		String sutHome = readArgumentValue(SUT);
		if(sutHome.endsWith(File.separator)) {
			sutHome = sutHome.substring(0, sutHome.length());
		}
		
		String tacocoHome = readOptionalArgumentValue(HOME, userDir);
		Launcher launcher = new Launcher(tacocoHome, sutHome);
		
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.sutHome);
		String projectName = readOptionalArgumentValue(PROJECT, probe.getId());
		launcher.setTacocoEnv();
		String parentCP = probe.getClasspath() + File.pathSeparator + launcher.getTacocoClasspath();
		
		launcher.startAnalysis(projectName, parentCP, launcher.sutHome, null);
	}

	/**

	 * @param projectName
	 * @param classpath
	 * @param sutHome
	 * @param jvmArgs
	 */
	private void startAnalysis(String projectName, String classpath, String sutHome, String[] jvmArgs) {
		final String defaultOutputPath =  
				PathBuilder.dirs(tacocoHome, "tacoco_output").buildFilePath();
		
		String outdir = readOptionalArgumentValue(OUTDIR, defaultOutputPath);
		if(!new File(outdir).exists()) {
			new File(outdir).mkdirs();
		}
		File err = new File(outdir, projectName+".err");
		File log = new File(outdir, projectName+".log");
		
		List<String> command = new ArrayList<>();
		command.add("java");

		command.add("-cp");
		command.add(classpath);
		command.add(argEquals(SUT, sutHome));
		command.add(argEquals(OUTDIR, outdir));
		if(readBooleanArgument(LOG)) {
			command.add(arg(LOG));
		}
		
		for(String jvmArg : jvmArgs) {
			if(jvmArg == null || jvmArg.isEmpty() || !jvmArg.startsWith("-")) {
				continue;
			}
			command.add(jvmArg);
		}
		
		String analyzerOptsFilePath = readArgumentValue(ANALYZER_OPTS);
		File analyzerOptsFile = new File(analyzerOptsFilePath);
		if(analyzerOptsFile == null 
				|| !analyzerOptsFile.exists()
				|| analyzerOptsFile.isDirectory()
				|| !analyzerOptsFile.isFile()) {
			throw new RuntimeException("Analyzer Options File is missing or "
					+ "is not a file -- " + analyzerOptsFilePath);
		}

		ArrayList<String> javaOptions = AnalysisOptions.readOptions(analyzerOptsFile);
		for(String option : javaOptions) {
			command.add(option);
		}
		
		command.add("org.spideruci.tacoco.analysis.AbstractAnalyzer");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(sutHome));
			
		//delete files before execution, 
		if(err.exists()) {
			err.delete();
		}

		if(log.exists()) {
			log.delete();
		}

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
		
//		String dbFile = outdir + File.separator + id + ".db";
//		if(System.getProperties().containsKey(DB)) {
//			try {
//				CreateSQLiteDB.dump(dbFile, targetDir, exec.toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		//run PIT test and produce mutation.xml
//		PITHandler h = new PITHandler();
//		if(System.getProperties().containsKey(PIT)){
//			h.runPit(id, classpath, targetDir, probe, outdir, tacocoHome);
//		}
//		
//		//update tacocoDB with PIT test info
//		if(System.getProperties().containsKey(PITDB)){
//			h.updateTacocoDB(outdir+"/"+id+".db",outdir+"/"+id);
//		}
	}

	private String getTacocoClasspath() throws Exception {
		final String tacocoCpPath = 
				new PathBuilder().path(tacocoHome, "cp.txt").buildFilePath();
		if(!new File(tacocoCpPath).exists()) {
			MavenCli mavenCli = new MavenCli();
			mavenCli.doMain(
					new String[] {"dependency:build-classpath", "-Dmdep.outputFile=cp.txt"}, 
					tacocoHome,
					System.out, 
					System.out);
		}

		final String cpDependencies = new String(Files.readAllBytes(Paths.get("cp.txt")));
		final String tacocoTargetPath = 
				new PathBuilder()
				.path(tacocoHome, "target", "classes")
				.buildFilePath();

		String tacocoClasspath = 
				new PathBuilder()
				.path(cpDependencies, tacocoTargetPath)
				.buildClassPath();
		return tacocoClasspath;
	}

	/*
	 * Move jacoco.jar from mvn repo to tacocoHome/lib
	 */
	private void setTacocoEnv() {
		final String dependencyLibPath = 
				new PathBuilder()
				.path(tacocoHome, "lib")
				.buildFilePath();
		
		if(new File(dependencyLibPath).exists()) {
			// TODO why return, why not delete the lib and then recreate this afresh?
			return;
		}
		
		MavenCli mavenCli = new MavenCli();
		
		mavenCli.doMain(
				new String[]{"dependency:copy-dependencies", "-DoutputDirectory=lib"}, 
				tacocoHome, 
				System.out, 
				System.out);
	}

}
