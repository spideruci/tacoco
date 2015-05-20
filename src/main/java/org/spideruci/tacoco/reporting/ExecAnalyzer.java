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
	  ExecAnalyzer execAnalyzer = processArgs(args);
	  execAnalyzer.dumpContent();
	}
	
  	private static ExecAnalyzer processArgs(String[] args) {
  	  File projectRoot = new File(args[0]);
      File execFile = new File(args[1]);
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
      ExecAnalyzer execAnalyzer = new ExecAnalyzer(execFile, parser);
  	  return execAnalyzer;
  	}
	
	private final File execFile;
	private final ExecutionDataParser parser;
	
	public ExecAnalyzer(File file, ExecutionDataParser parser) {
	  this.execFile = file;
	  this.parser = parser;
	}

	public void dumpContent() throws IOException {
	  System.out.printf("exec:%s%n", execFile);
		final FileInputStream in = new FileInputStream(execFile);
		final ExecutionDataReader reader = new ExecutionDataReader(in);
		
		reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
			public void visitSessionInfo(final SessionInfo info) {
			  String nextSessionName = info.getId();
			  parser.resetExecDataStore(nextSessionName);
				System.out.println("\n" + nextSessionName);
			}
		});
		
		reader.setExecutionDataVisitor(parser);
		reader.read();
		in.close();
		parser.close();
	}
	
	
}
