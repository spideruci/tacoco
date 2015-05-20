package org.spideruci.tacoco.reporting;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

public class ExecDataPrintManager {
  
  private final PrintStream jsonOut;
  private final LineCoverageFormat format;
  private final boolean isPrettyPrint;
  
  @SuppressWarnings("resource")
  public static ExecDataPrintManager createPrintManager(final String jsonFilePath,
      final String formatString,
      final String prettyString) {
    
    PrintStream out = null;
    
    if(jsonFilePath == null || jsonFilePath.isEmpty()) {
      out = System.out;
    } else {
      File jsonFile = new File(jsonFilePath);
      try {
        jsonFile.createNewFile();
        if(jsonFile.exists() && !jsonFile.isFile()) {
          System.err.println("Json file path is not a file. Switching to STDOUT");
          out = System.out;
        } else {
          out = new PrintStream(jsonFile);
        }
      } catch (IOException e) {
        out = System.out;
        e.printStackTrace();
      }
    }
    
    LineCoverageFormat format = 
        (formatString == null || formatString.isEmpty()) 
        ? LineCoverageFormat.DENSE : LineCoverageFormat.valueOf(formatString);
    boolean isPretty = 
        (prettyString == null || prettyString.isEmpty()) 
        ? false : Boolean.parseBoolean(prettyString);
    ExecDataPrintManager printMgr = 
        new ExecDataPrintManager(out, format, isPretty);
    return printMgr;
  }
  
  private ExecDataPrintManager(PrintStream jsonOut, LineCoverageFormat format, 
      boolean isPrettyPrint) {
    this.jsonOut = jsonOut;
    this.format = format;
    this.isPrettyPrint = isPrettyPrint;
  }
  
  public void closePrintStream() {
    jsonOut.flush();
    jsonOut.close();
  }
  
  public PrintStream jsonOut() {
    return jsonOut;
  }
  
  public boolean isPrettyPrint() {
    return isPrettyPrint;
  }
  
  public LineCoverageFormat format() {
    return format;
  }

}
