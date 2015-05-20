package org.spideruci.tacoco.reporting.data;


public class SourceFileCoverage {
  
  private final String name;
  private final String packagename;
  private final LineCoverageFormat format;
  private final int firstLine;
  private final int[] lines;
  
  SourceFileCoverage(String fileName, String packageName, int firstLine, 
      LineCoverageFormat format, int[] linesCoverage) {
    this.name = fileName;
    this.packagename = packageName;
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
  public int[] getLinesCoverage() {
    return lines;
  }

  public static enum LineCoverageFormat {
    LOOSE, COMPACT, DENSE
  }

}
