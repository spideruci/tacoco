package org.spideruci.tacoco.mutation;

import static org.spideruci.tacoco.cli.AbstractCli.PIT_JAR;
import static org.spideruci.tacoco.cli.AbstractCli.PIT_MAX_MUTATIONS_PER_CLASS;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
	private File pitErrFile;
	private String maxMutationsPerClass;
	
	private final int MAX_TRY = 5;

	
	
	public PITHandler(AbstractBuildProbe probe, String outdir, String outFileName, String sutHome){
		this.probe = probe;
		this.outdir = outdir;
		this.outFileName = outFileName;
		this.pit_jar_cp = readOptionalArgumentValue(PIT_JAR, "");
		this.maxMutationsPerClass = readOptionalArgumentValue(PIT_MAX_MUTATIONS_PER_CLASS, "0");
		
		this.sutHome = sutHome;
		this.pitDB = new PITDB();
		this.pitErrFile = new File(outdir, outFileName+".pit.err");
	}
	

	public void run(){
		for(int i=0; i<MAX_TRY; ++i){
			runPit();
			if(hasGreenSuite()) break;
		}
	}

	private boolean hasGreenSuite() {
		Set<String> set = getPITexcludeTests();
		if(set == null || set.size() == 0) return true;
		return false;
	}


	private void runPit() {
		StringBuffer testClasses= new StringBuffer();
		StringBuffer classes= new StringBuffer();

		Set<String> excludeTests = null;
		if(pitErrFile.exists()){
			excludeTests = getPITexcludeTests();
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

		//System.out.println("cp: " + this.pit_jar_cp+":"+this.probe.getClasspath());
		//System.out.println("testClasses: " + testClasses);
		
		
		ProcessBuilder pitRunner = new ProcessBuilder(
				"java",
				"-cp", this.pit_jar_cp+":"+this.probe.getClasspath(),
				"-Xms2048M",
				"org.pitest.mutationtest.commandline.MutationCoverageReport",
				"--reportDir="+outdir+File.separator+outFileName,
				"--targetClasses="+classes,
				"--targetTests="+testClasses,
				"--sourceDirs="+this.sutHome,
				"--outputFormats=XML",
				"--threads=4",
				"--maxMutationsPerClass="+this.maxMutationsPerClass);

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

	private Set<String> getPITexcludeTests() {

		Set<String> set = new HashSet<>();
		Pattern p = Pattern.compile("testClass=.*,");

		try {
			for(String line:Files.readAllLines(Paths.get(pitErrFile.toURI()), StandardCharsets.UTF_8)){
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
