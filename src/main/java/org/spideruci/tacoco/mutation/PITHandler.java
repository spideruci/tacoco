package org.spideruci.tacoco.mutation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PITHandler {
	
	private PITDB pitDB;

	public void runPit(String id, String classpath, String targetDir, AbstractBuildProbe probe, String outdir, String tacocoHome) {
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

		String pitPath = tacocoHome+"/lib/pitest-command-line-1.1.7.jar:"
				+tacocoHome+"/lib/pitest-1.1.7-SNAPSHOT.jar";

		File err = new File(outdir, id+".pit.err");
		File log = new File(outdir, id+".pit.log");
		if(err.exists()) err.delete();
		if(log.exists()) log.delete();


		ProcessBuilder pitRunner = new ProcessBuilder(
				"java",
				"-cp", pitPath+":"+classpath,
				"-Xms2048M",
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

	public void updateTacocoDB(String tacocodb, String pitReportDir) {
		this.pitDB.updateTacocoDB(tacocodb, pitReportDir);
	}
}
