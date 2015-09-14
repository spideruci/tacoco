package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.CliAble.HOME;
import static org.spideruci.tacoco.cli.CliAble.TARGET;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readArgumentValue;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.spideruci.tacoco.AbstractBuildProbe.Child;

public class TacocoLauncher {

	private String tacocoHome, targetDir;
	private static String tacocoClasspath = null;

	private TacocoLauncher(String tacocoHome, String targetDir){
		this.tacocoHome = tacocoHome;
		this.targetDir = targetDir;
	}

	public static void main(String[] args) throws Exception{

		TacocoLauncher launcher = new TacocoLauncher(readOptionalArgumentValue(HOME,System.getProperty("user.dir"))
													,readArgumentValue(TARGET));
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.targetDir);
		launcher.setTacocoEnv();
		String parentCP = probe.getClasspath() +":"+ launcher.getTacocoClasspath(); 

		int i=0;
		if(probe.hasChild()){	
			for(Child child : probe.getChildren()){
				if(++i==4)
				launcher.startJUnitRunner(child.classpath+":"+ parentCP, child.targetDir, child.jvmArgs);
			}
		}
		launcher.startJUnitRunner(parentCP, launcher.targetDir, null);
	}

	
	private void startJUnitRunner(String classpath, String targetDir, String[] jvmArgs) {
		
		ProcessBuilder builder = new ProcessBuilder(
				"java",
				"-cp", classpath,
				"-Xmx1536M",// "-Duser.language=hi", "-Duser.country=IN",
				"-javaagent:"+tacocoHome+"/lib/org.jacoco.agent-0.7.4.201502262128-runtime.jar=destfile=jacoco.exec,dumponexit=false",
				"-Dtacoco.target="+targetDir,
				"-Dtacoco.log=on",
				"-Dtacoco.thread="+1,
				"org.spideruci.tacoco.JUnitRunner");//.inheritIO();
		builder.directory(new File(targetDir));
		builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		builder.redirectError(new File("tacoco.err"));
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
