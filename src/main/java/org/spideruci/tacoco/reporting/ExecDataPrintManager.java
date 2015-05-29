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
  public static ExecDataPrintManager createPrintManager(
      final String jsonFilePath, final String formatString, 
      final boolean prettyPrint) {
    PrintStream out = null;
    if(jsonFilePath == null || jsonFilePath.isEmpty()) {
      out = System.out;
    } else {
      File jsonFile = new File(jsonFilePath);
      try {
        jsonFile.createNewFile();
        if(jsonFile.exists() && !jsonFile.isFile()) {
          System.err.printf("Json-output destination (%s) is not a file. "
              + "Switching to STDOUT%n", jsonFile.getPath());
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
    boolean isPretty = prettyPrint;
    ExecDataPrintManager printMgr = 
        new ExecDataPrintManager(out, format, isPretty);
    System.out.printf("json:%s,format:%s,pretty:%s%n", 
        jsonFilePath, formatString, prettyPrint);
    return printMgr;
  }
  
  private ExecDataPrintManager(PrintStream jsonOut, LineCoverageFormat format, 
      boolean isPrettyPrint) {
    this.jsonOut = jsonOut;
    this.format = format;
    this.isPrettyPrint = isPrettyPrint;
  }
  
  public void closeJsonStream() {
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
