package org.spideruci.tacoco;

<<<<<<< HEAD
import static org.spideruci.tacoco.cli.AbstractCli.DB;
import static org.spideruci.tacoco.cli.AbstractCli.HELP;
import static org.spideruci.tacoco.cli.AbstractCli.HOME;
import static org.spideruci.tacoco.cli.AbstractCli.INST;
import static org.spideruci.tacoco.cli.AbstractCli.INST_ARGS;
import static org.spideruci.tacoco.cli.AbstractCli.LANUCHER_CLI;
import static org.spideruci.tacoco.cli.AbstractCli.NOJUNIT;
import static org.spideruci.tacoco.cli.AbstractCli.OUTDIR;
import static org.spideruci.tacoco.cli.AbstractCli.PIT;
import static org.spideruci.tacoco.cli.AbstractCli.PROJECT;
import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;
=======
import org.apache.maven.cli.MavenCli;
import org.spideruci.tacoco.AbstractBuildProbe.Child;
import org.spideruci.tacoco.db.CreateSQLiteDB;
>>>>>>> 92b024e886feea5f215d40c9dc18e116ecaff90a

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		if(targetDir.endsWith("/")) targetDir = targetDir.substring(0, targetDir.length());
		TacocoLauncher launcher = new TacocoLauncher(readOptionalArgumentValue(HOME, USER_DIR)
				,targetDir);
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.targetDir);
		String name = readOptionalArgumentValue(PROJECT, probe.getId());

		launcher.setTacocoEnv();
		String parentCP = launcher.getTacocoClasspath() +":"+ probe.getClasspath(); 
		//String parentCP = probe.getClasspath() +":"+ launcher.getTacocoClasspath()  ; 

		
		if(probe.hasChild()){	
			for(Child child : probe.getChildren()){
				launcher.startJUnitRunner(name+"."+child.id, child.classpath+":"+ parentCP, child.targetDir, child.jvmArgs, probe);
			}
		}
		launcher.startJUnitRunner(name, parentCP, launcher.targetDir, null, probe);
	}


	private void startJUnitRunner(String id, String classpath, String targetDir, String[] jvmArgs, AbstractBuildProbe probe) {

		String outdir = readOptionalArgumentValue(OUTDIR, tacocoHome+"/tacoco_output");

		if(!new File(outdir).exists()) new File(outdir).mkdirs();
		
		File exec = new File(outdir, id+".exec");
		File err = new File(outdir, id+".err");
		File log = new File(outdir, id+".log");
		
		
		
		final String instrumenterLocation = readOptionalArgumentValue(INST, 
				tacocoHome+"/lib/org.jacoco.agent-0.7.4.201502262128-runtime.jar");
		final String instrumentedArgs = readOptionalArgumentValue(INST_ARGS, 
				"destfile=" + outdir + "/" + id + ".exec" + ",dumponexit=false");

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
		
		if(!System.getProperties().containsKey(NOJUNIT)){
			
			//delete files before execution, 
			if(exec.exists()) exec.delete();
			if(err.exists()) err.delete();
			if(log.exists()) log.delete();

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
		}

		String dbFile = outdir+"/"+id+".db";
		if(System.getProperties().containsKey(DB))
			try {
				CreateSQLiteDB.dump(dbFile, targetDir, exec.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		
		if(System.getProperties().containsKey(PIT)){
			runPit(id, classpath, targetDir, probe, outdir);
		}

	}

	private void runPit(String id, String classpath, String targetDir, AbstractBuildProbe probe, String outdir) {
		StringBuffer testClasses= new StringBuffer();
		StringBuffer classes= new StringBuffer();
		
		Set<String> excludeTests = null;
		File pitErrFile = new File(outdir, id+".pit.err");
		if(pitErrFile.exists()){
			excludeTests = getPITexcludeTests(pitErrFile);
		}
		
		for(String s : probe.getTestClasses()){
			if(excludeTests != null && excludeTests.contains(s)) continue;
			testClasses.append(s+",");
		}
		
		for(String s : probe.getClasses()){
			classes.append(s+",");
		}
		
		String pitPath = this.tacocoHome+"/lib/pitest-command-line-1.1.7.jar:"
						+this.tacocoHome+"/lib/pitest-1.1.7.jar";
		
		File err = new File(outdir, id+".pit.err");
		File log = new File(outdir, id+".pit.log");
		if(err.exists()) err.delete();
		if(log.exists()) log.delete();
		
		
		ProcessBuilder pitRunner = new ProcessBuilder(
				"java",
				"-cp", classpath+":"+pitPath,
				"-Xmx2048M",
				"org.pitest.mutationtest.commandline.MutationCoverageReport",
			    "--reportDir="+outdir+"/"+id,
			    "--targetClasses="+classes,
			    "--targetTests="+testClasses,
			    "--sourceDirs="+targetDir+"/src",
			    "--outputFormats=XML");
		pitRunner.directory(new File(targetDir));
		pitRunner.redirectError(err);
		pitRunner.redirectOutput(log);

		final Process pit;
		try{
			pit= pitRunner.start();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					pit.destroy();
				}
			}); 
			pit.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private Set<String> getPITexcludeTests(File pitErrFile) {
		
		Set<String> set = new HashSet<>();
		Pattern p = Pattern.compile("testClass=.*,");
		
		try {
			for(String line:Files.readAllLines(Paths.get(pitErrFile.toURI()))){
				if(line.endsWith("did not pass without mutation.")){
					Matcher m = p.matcher(line);
					if(m.find()) {
						String match = m.group(0);
						String exClass = match.substring(10,match.length()-1);
						set.add(exClass);
					}
				}
			}
			
			
		} catch (IOException e) {
			set = null;
		}
		
		return set;
	}

	private String getTacocoClasspath() throws Exception{

		if(tacocoClasspath != null) return tacocoClasspath;
		if(!new File(tacocoHome+"/cp.txt").exists()) {
            MavenCli mavenCli = new MavenCli();
            mavenCli.doMain(new String[] {"dependency:build-classpath", "-Dmdep.outputFile=cp.txt"}, tacocoHome,
                    System.out, System.out);
		}
		tacocoClasspath = new String(Files.readAllBytes(Paths.get("cp.txt")))+":"+ tacocoHome + "/target/classes";
		return tacocoClasspath;
	}

	/*
	 * Move jacoco.jar from mvn repo to tacocoHome/lib
	 */
	private void setTacocoEnv() {
		if(new File(tacocoHome+"/lib").exists()) return;
        MavenCli mavenCli = new MavenCli();
        mavenCli.doMain(new String[]{"dependency:copy-dependencies", "-DoutputDirectory=lib"}, tacocoHome, System.out, System.out);
	}

}
