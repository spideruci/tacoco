package org.spideruci.tacoco.coverage;

import java.util.ArrayList;
import java.util.HashMap;

import org.jacoco.core.analysis.ICounter;
import org.spideruci.tacoco.reporting.data.LineCoverageCoder;
import org.spideruci.tacoco.reporting.data.LinesStatusCoder;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;

public class CoverageMatrix {
  
  private final HashMap<String, Integer> testNameIndex;
  private final HashMap<SourceFile, Integer> sourceFileIndex;
  private final ArrayList<Integer> sourceFileRanges;
  private final LineCoverageFormat format;
  private final ArrayList<int[]> testStmtMatrix;
  
  public CoverageMatrix(LineCoverageFormat fmt) {
    testNameIndex = new HashMap<>();
    sourceFileIndex = new HashMap<>();
    sourceFileRanges = new ArrayList<>();
    format = fmt;
    testStmtMatrix = new ArrayList<>();
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
  
  public int getTestCount() {
    if(testStmtMatrix == null) return 0;
    return testStmtMatrix.size();
  }
  
  public void addStmtCoverage(String testName, int[] coverage) {
    @SuppressWarnings("unused") int index = testNameIndex.get(testName);
    testStmtMatrix.add(coverage);
  }
  
  public void printMatrix() {
    System.out.println(format);
    for(int[] test : testStmtMatrix) {
      int[] decodedCoverge = 
          (format == LineCoverageFormat.DENSE) 
          ? decodeDense(test) : decodeCompact(test);
      for(int coverage : decodedCoverge) {
        System.out.print(coverage + " ");
      }
      System.out.println();
    }
  }
  
  public boolean[][] toBooleanMatrix() {
    int testsNum = this.testStmtMatrix.size();
    boolean[][] testStmtMatrix = new boolean[testsNum][];
    int testCount = 0;
    for(int[] test : this.testStmtMatrix) {
      int[] decodedCoverge = null;
      if(format == LineCoverageFormat.DENSE) {
        decodedCoverge = decodeDense(test); 
      } else {
        decodedCoverge = decodeCompact(test);
      }
      boolean[] stmts = new boolean[decodedCoverge.length];
      for(int i = 0; i < decodedCoverge.length; i += 1) {
        int coverageStatus = decodedCoverge[i];
        if(coverageStatus== ICounter.FULLY_COVERED
            || coverageStatus == ICounter.PARTLY_COVERED) {
          stmts[i] = true;
        } else {
          stmts[i] = false;
        }
      }
      testStmtMatrix[testCount] = stmts;
      testCount += 1;
    }
    return testStmtMatrix;
  }
  
  public boolean[] getCoverableStmts() {
    int testsNum = this.getTestCount();
    if(testsNum == 0) return new boolean[0];

    int[] test = this.testStmtMatrix.get(0);

    int[] decodedCoverge = null;
    if(format == LineCoverageFormat.DENSE) {
      decodedCoverge = decodeDense(test); 
    } else {
      decodedCoverge = decodeCompact(test);
    }
    
    boolean[] stmts = new boolean[decodedCoverge.length];
    for(int i = 0; i < decodedCoverge.length; i += 1) {
      int coverageStatus = decodedCoverge[i];
      if(coverageStatus == ICounter.EMPTY) {
        stmts[i] = false;
      } else {
        stmts[i] = true;
      }
    }
    
    return stmts;
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

    

  

}
