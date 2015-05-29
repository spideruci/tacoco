package org.spideruci.tacoco.reporting;

import static org.spideruci.tacoco.reporting.ExecDataPrintManager.createPrintManager;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
	  
	  if(System.getProperties().containsKey(HELP)) {
	    printAnalyzerHelp();
	  }
	  
	  ExecAnalyzer execAnalyzer = processArgs(args);
	  execAnalyzer.dumpContent();
	}
	
  	private static ExecAnalyzer processArgs(String[] args) {
  	  
  	  String sut = readArgumentValue(SUT);
  	  File projectRoot = new File(sut);
  	  
  	  String exec = readArgumentValue(EXEC);
      File execFile = new File(exec);
      
      String jsonFilePath = readOptionalArgumentValue(JSON, null);
      
      String format = System.getProperty(FMT, "DENSE");
      boolean prettyPrint = System.getProperties().containsKey(PP);
      
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
		while(reader.read()) {};
		
		parser.resetExecDataStore(parser.getCoverageTitle());
		parser.forcePrintEnd();
		
		in.close();
		parser.close();
	}
	
	
}
