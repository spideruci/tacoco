package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.CliAble.HOME;
import static org.spideruci.tacoco.cli.CliAble.TARGET;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readArgumentValue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TacocoLauncher {

	private String tacocoHome, targetDir;
	private static String tacocoClasspath = null;

	private TacocoLauncher(String tacocoHome, String targetDir){
		this.tacocoHome = tacocoHome;
		this.targetDir = targetDir;
	}

	public static void main(String[] args) throws Exception{

		TacocoLauncher launcher = new TacocoLauncher(readArgumentValue(HOME),readArgumentValue(TARGET));
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.targetDir);
		String classpath = probe.getClasspath() +":"+ launcher.getTacocoClasspath();
		launcher.setTacocoEnv();
		launcher.startJUnitRunner(classpath);
	}

	
	private void startJUnitRunner(String classpath) {
		ProcessBuilder builder = new ProcessBuilder(
				"java",
				"-cp", classpath,
				"-javaagent:"+tacocoHome+"/lib/org.jacoco.agent-0.7.4.201502262128-runtime.jar=destfile=jacoco.exec,dumponexit=false",
				"-Dtacoco.home="+tacocoHome,
				"-Dtacoco.target="+targetDir,
				"org.spideruci.tacoco.JUnitRunner").inheritIO();
		builder.directory(new File(targetDir));
		try{
			Process p = builder.start();
			p.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String getTacocoClasspath() throws Exception{
		
		if(tacocoClasspath != null) return tacocoClasspath;
		if(!new File(tacocoHome+"/cp.txt").exists()) {
			ProcessBuilder builder = new ProcessBuilder(
					"/usr/local/bin/mvn","dependency:build-classpath","-Dmdep.outputFile=cp.txt").inheritIO();
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
		ProcessBuilder builder = new ProcessBuilder("/usr/local/bin/mvn","dependency:copy-dependencies","-DoutputDirectory=lib").inheritIO();
		builder.directory(new File(tacocoHome));
		try{
			Process p = builder.start();
			p.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
