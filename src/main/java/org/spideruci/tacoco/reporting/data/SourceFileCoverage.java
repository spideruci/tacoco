package org.spideruci.tacoco.reporting.data;


public class SourceFileCoverage<T> {
  
  private final String name;
  private final String packagename;
  private final String sessionName;
  private final LineCoverageFormat format;
  private final int firstLine;
  private final T[] lines;
  
  SourceFileCoverage(String fileName, String packageName, String sessionName, 
      int firstLine, LineCoverageFormat format, T[] linesCoverage) {
    this.name = fileName;
    this.packagename = packageName;
    this.sessionName = sessionName;
    this.firstLine = firstLine;
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

  public static enum LineCoverageFormat {
    LOOSE, COMPACT, DENSE
  }

}
