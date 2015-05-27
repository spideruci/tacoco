package org.spideruci.tacoco.reporting.data;


public class SourceFileCoverage<T> {
  
  private final String name;
  private final String packagename;
  private final String sessionName;
  private final LineCoverageFormat format;
  private final int firstLine, lastLine;
  private final T[] lines;
  
  public SourceFileCoverage() {
    lines = null;
    format = null;
    firstLine = lastLine = -1;
    name = packagename = sessionName = null;
  }
  
  SourceFileCoverage(String fileName, String packageName, String sessionName, 
      int firstLine, int lastLine, LineCoverageFormat format, 
      T[] linesCoverage) {
    this.name = fileName;
    this.packagename = packageName;
    this.sessionName = sessionName;
    this.firstLine = firstLine;
    this.lastLine = lastLine;
    this.format = format;
    this.lines = linesCoverage;
  }
  
  /**
   * @return the fileName
   */
  public String getFileName() {
    return name;
  }

  /**
   * @return the packageName
   */
  public String getPackageName() {
    return packagename;
  }
  
  /**
   * @return the sessionName
   */
  public String getSessionName() {
    return sessionName;
  }

  /**
   * @return the format
   */
  public LineCoverageFormat getFormat() {
    return format;
  }

  /**
   * @return the offset
   */
  public int getFirstLine() {
    return firstLine;
  }

  /**
   * @return the linesCoverage
   */
  public T[] getLinesCoverage() {
    return lines;
  }

  /**
   * @return the lastLine
   */
  public int getLastLine() {
    return lastLine;
  }

  public static enum LineCoverageFormat {
    LOOSE, COMPACT, DENSE
  }

}
