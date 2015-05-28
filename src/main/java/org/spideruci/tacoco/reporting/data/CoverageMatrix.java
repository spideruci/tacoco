package org.spideruci.tacoco.reporting.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;

public class CoverageMatrix {
  
  private final HashMap<String, Integer> testNameIndex;
  private final HashMap<SourceFile, Integer> sourceFileIndex;
  private final ArrayList<Integer> sourceFileRanges;
  private final LineCoverageFormat format;
  private final ArrayList<int[]> testStmtmatrix;
  
  public CoverageMatrix(LineCoverageFormat fmt) {
    testNameIndex = new HashMap<>();
    sourceFileIndex = new HashMap<>();
    sourceFileRanges = new ArrayList<>();
    format = fmt;
    testStmtmatrix = new ArrayList<>();
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
    @SuppressWarnings("unused") int index = testNameIndex.get(testName);
    testStmtmatrix.add(coverage);
  }
  
  public void printMatrix() {
    System.out.println(format);
    for(int[] test : testStmtmatrix) {
      int[] decodedCoverge = 
          (format == LineCoverageFormat.DENSE) 
          ? decodeDense(test) : decodeCompact(test);
      for(int coverage : decodedCoverge) {
        System.out.print(coverage + " ");
      }
      System.out.println();
    }
  }
  
  public void dumpMatrix() {
    Gson gson = new Gson();
    String json = gson.toJson(this);
    System.out.println(json);
  }
  
    private int[] decodeCompact(int[] codedCoverages) {
      LineCoverageCoder coder = new LineCoverageCoder();
      int[] decodedCoverage = new int[codedCoverages.length];
      
      int count = 0;
      for(int codedCoverage : codedCoverages) {
        int[] counts = coder.decode(codedCoverage);
        int status = coder.toStatus(counts);
        decodedCoverage[count] = status;
        count += 1;
      }
      
      return decodedCoverage;
    }
    
    private int[] decodeDense(int[] codedCoverage) {
      LinesStatusCoder coder = new LinesStatusCoder();
      int[] decodedCoverage = coder.decode(codedCoverage);
      return decodedCoverage;
    }

  public static class SourceFile {
    final String fullName;
    final int firstLine;
    final int lastLine;
    
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
