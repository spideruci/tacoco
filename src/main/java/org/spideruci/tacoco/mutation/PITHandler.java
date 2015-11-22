package org.spideruci.tacoco.mutation;

import static org.spideruci.tacoco.cli.AbstractCli.PIT_JAR;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spideruci.tacoco.probe.AbstractBuildProbe;

public class PITHandler {
	
	private PITDB pitDB;
	private AbstractBuildProbe probe;
	private String outdir;
	private String pit_jar_cp;
	private String outFileName;
	private String sutHome;
	
	/*
	private String name;
	private String outDir;
	private File exec;
	protected String dbFileName;
	*/
	
	
	public PITHandler(AbstractBuildProbe probe, String outdir, String outFileName, String sutHome){
		this.probe = probe;
		this.outdir = outdir;
		this.outFileName = outFileName;
		this.pit_jar_cp = readOptionalArgumentValue(PIT_JAR, "");
		this.sutHome = sutHome;
		this.pitDB = new PITDB();
	}
	
	public void runPit() {
		StringBuffer testClasses= new StringBuffer();
		StringBuffer classes= new StringBuffer();

		Set<String> excludeTests = null;
		File pitErrFile = new File(outdir, outFileName+".pit.err");
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

		File err = new File(outdir, outFileName+".pit.err");
		File log = new File(outdir, outFileName+".pit.log");
		if(err.exists()) err.delete();
		if(log.exists()) log.delete();

		System.out.println(testClasses);
		System.out.println(this.probe.getClasspath());
		
		//System.exit(0);
		
		ProcessBuilder pitRunner = new ProcessBuilder(
				"java",
				"-cp", this.pit_jar_cp+":"+this.probe.getClasspath(),
				"-Xms2048M",
				"org.pitest.mutationtest.commandline.MutationCoverageReport",
				"--reportDir="+outdir+File.separator+outFileName,
				"--targetClasses="+classes,
				"--targetTests="+testClasses,
				"--sourceDirs="+this.sutHome,
				"--outputFormats=XML");
		pitRunner.directory(new File(this.sutHome));
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

	public void updateTacocoDB() {
		String dbFile = this.outdir + File.separator + this.outFileName + ".db";
		String reportDir = this.outdir + File.separator + this.outFileName;
		
		try {
			this.pitDB.updateTacocoDB(dbFile, reportDir);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
