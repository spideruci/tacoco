package org.spideruci.tacoco.reporting;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

public class ExecDataPrintManager {
  
  private final PrintStream jsonOut;
  private final LineCoverageFormat format;
  private final boolean isPrettyPrint;
  
  public static ExecDataPrintManager createPrintManager(final String jsonFilePath,
      final String formatString,
      final String prettyString) {
    PrintStream out;
    try {
      out = (jsonFilePath == null || jsonFilePath.isEmpty())
      ? System.out : new PrintStream(jsonFilePath);
    } catch (FileNotFoundException e) {
      out = System.out;
      System.err.printf("%s not found. "
          + "Switching to Standard out.%n", jsonFilePath);
      e.printStackTrace();
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
