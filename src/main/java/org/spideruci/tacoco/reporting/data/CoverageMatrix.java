package org.spideruci.tacoco.reporting.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

public class CoverageMatrix {
  
  private final HashMap<String, Integer> testNameIndex;
  private final HashMap<SourceFile, Integer> sourceFileIndex;
  private final ArrayList<Integer> sourceFileRanges;
  private final LineCoverageFormat format;
  private final int[][] testStmtmatrix;
  
  public CoverageMatrix(LineCoverageFormat fmt, int testCount) {
    testNameIndex = new HashMap<>();
    sourceFileIndex = new HashMap<>();
    sourceFileRanges = new ArrayList<>();
    format = fmt;
    testStmtmatrix = new int[testCount][];
  }

  private int nextAvailSourceId = 0;
  public int indexSourceFile(SourceFile source) {
    Integer sourceId = sourceFileIndex.get(source);
    if(sourceId != null) {
      return sourceId;
    } 

    sourceFileIndex.put(source, nextAvailSourceId);
    nextAvailSourceId += 1;
    return sourceFileIndex.get(source);
  }

  private int nextAvailTestId = 0;
  public int indexTestName(String testName) {
    Integer testId = testNameIndex.get(testName);
    if(testId != null) {
      return testId;
    } 

    testNameIndex.put(testName, nextAvailTestId);
    nextAvailTestId += 1;
    return testNameIndex.get(testName);
  }

  /**
   * @return the sourceFileRanges
   */
  public void indexSourceFileRanges(SourceFile source, int rangeEnd) {
    int sourceId = sourceFileIndex.get(source);
    assert sourceId == sourceFileRanges.size();
    sourceFileRanges.add(rangeEnd);
  }
  
  /**
   * @return the format
   */
  public LineCoverageFormat getFormat() {
    return format;
  }
  
  public void addStmtCoverage(String testName, int[] coverage) {
    int index = testNameIndex.get(testName);
    testStmtmatrix[index] = coverage;
  }
  
  public void printMatrix() {
    for(int[] test : testStmtmatrix) {
      for(int codedCoverage : test) {
        System.out.print(codedCoverage + "\t");
      }
      System.out.println();
    }
  }

  public static class SourceFile {
    private final String fullName;
    private final int firstLine;
    private final int lastLine;
    
    public SourceFile(String fullName, int firstLine, int lastLine) {
      this.fullName = fullName;
      this.firstLine = firstLine;
      this.lastLine = lastLine;
    }
    
    public String getFullName() {
      return this.fullName;
    }

    @Override
    public int hashCode() {
      return this.fullName.hashCode();
    }
    
    @Override
    public boolean equals(Object object) {
      if(object == null) {
        return false;
      }
      
      if(!(object instanceof SourceFile)) {
        return false;
      }
      
      SourceFile sourceFile = (SourceFile) object;
      
      if(this.fullName.equals(sourceFile.fullName)) {
        return true;
      }
      
      return false;
    }

    /**
     * @return the firstLine
     */
    public int getFirstLine() {
      return firstLine;
    }

    /**
     * @return the lastLine
     */
    public int getLastLine() {
      return lastLine;
    }
  }

}
