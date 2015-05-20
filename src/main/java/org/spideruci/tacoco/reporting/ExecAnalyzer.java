package org.spideruci.tacoco.reporting;

import static org.spideruci.tacoco.reporting.ExecDataPrintManager.createPrintManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;

public final class ExecAnalyzer {
  
	/**
	 * Reads all execution data files specified as the arguments and dumps the
	 * content.
	 * 
	 * @param args
	 *            
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
	  File projectRoot = new File(args[0]);
	  String execFile = args[1];
	  String jsonFilePath = args.length < 3 ? null : args[2];
	  String format = 
	      (args.length < 4 || args[3] == null) 
	      ? null : args[3].trim().toUpperCase();
	  String prettyPrint = 
	      (args.length < 5 || args[4] == null) 
	      ? null : args[4].trim().toLowerCase();
	  ExecDataPrintManager printManager = 
	      createPrintManager(jsonFilePath, format, prettyPrint);
	  ExecutionDataParser parser = 
	      new ExecutionDataParser(projectRoot, printManager);
	  ExecAnalyzer execAnalyzer = new ExecAnalyzer();
	  execAnalyzer.dumpContent(execFile, parser, jsonFilePath, format, prettyPrint);
	}

	private void dumpContent(final String file,
	    final ExecutionDataParser parser,
	    final String jsonFilePath,
	    final String formatString,
	    final String prettyString) throws IOException {
		System.out.printf("exec:%s,json:%s,format:%s,pretty:%s%n", 
		    file, jsonFilePath, formatString, prettyString);
		
		final FileInputStream in = new FileInputStream(file);
		final ExecutionDataReader reader = new ExecutionDataReader(in);
		
		reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
			public void visitSessionInfo(final SessionInfo info) {
			  String sessionName = String.format("Session \"%s\": %s - %s", 
			      info.getId(),
            new Date(info.getStartTimeStamp()),
            new Date(info.getDumpTimeStamp()));
			  parser.resetExecDataStore();
			  parser.setCoverageTitle(sessionName);
				System.out.println("\n" + sessionName);
			}
		});
		
		reader.setExecutionDataVisitor(parser);
		reader.read();
		
		
		in.close();
		System.out.println();
	}
}
