package org.spideruci.tacoco.reporting.data;


public class SourceFileCoverage {
  
  private final String fileName;
  private final String packageName;
  private final LineCoverageFormat format;
  private final int firstLine;
  private final int[] linesCoverage;
  
  SourceFileCoverage(String fileName, String packageName, int firstLine, 
      LineCoverageFormat format, int[] linesCoverage) {
    this.fileName = fileName;
    this.packageName = packageName;
    this.firstLine = firstLine;
    this.format = format;
    this.linesCoverage = linesCoverage;
  }
  
  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @return the packageName
   */
  public String getPackageName() {
    return packageName;
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
    return linesCoverage;
  }

  public static enum LineCoverageFormat {
    LOOSE, COMPACT, DENSE
  }

}
