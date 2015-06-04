package org.spideruci.tacoco.coverage;

import java.util.ArrayList;

import org.jacoco.core.analysis.ICounter;
import org.spideruci.tacoco.reporting.data.LineCoverageCoder;
import org.spideruci.tacoco.reporting.data.LinesStatusCoder;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

public class SourceSpecificCoverageMatrix {
  
  private final SourceFile source;
  private final ArrayList<Integer> activatingTests;
  private final ArrayList<boolean[]> testStmtMatrix;
  private final boolean[] coverableLines;
  
  public SourceSpecificCoverageMatrix(final SourceFile source) {
    this.source = source;
    activatingTests = new ArrayList<>();
    testStmtMatrix = new ArrayList<>();
    coverableLines = new boolean[source.getRealLineCount()];
  }

  /**
   * @return the source unit's name
   */
  public String getSourceName() {
    return source.fullName;
  }
  
  /**
   * @return the source unit's first line
   */
  public int getSourceFline() {
    return source.firstLine;
  }
  
  /**
   * @return the source unit's last line
   */
  public int getSourceLline() {
    return source.lastLine;
  }
  
  /**
   * 
   * @return 
   * count of test cases that activate (execute) any part of this source unit.
   */
  public int getActivatingTestCount() {
    return this.activatingTests.size();
  }

  public void addStmtCoverage(int testId, int[] coverage, 
      LineCoverageFormat format, boolean findCoverables) {
    if(findCoverables) {
      int length = coverableLines.length; 
      boolean[] coverableLines = 
          findCoverableStmts(coverage, format, this.coverableLines.length);
      for(int i = 0; i < length; i += 1) {
        this.coverableLines[i] = coverableLines[i];
      }
    }
    
    boolean[] booleanCoverage = createCoverageVectors(coverage, format);
    if(!isTestRelevant(booleanCoverage)) {
      return;
    }
    this.activatingTests.add(testId);
    this.testStmtMatrix.add(booleanCoverage);
  }
  
  boolean isTestRelevant(boolean[] booleanCoverage) {
    for(boolean iscovered : booleanCoverage) { 
      if(iscovered) {
        return true;
      }
    }
    return false;
  }
  
  boolean[] createCoverageVectors(int[] coverage, LineCoverageFormat format) {
    int[] decodedCoverge = null;
    if(format == LineCoverageFormat.DENSE) {
      decodedCoverge = decodeDense(coverage); 
    } else {
      decodedCoverge = decodeCompact(coverage);
    }
    
    int sourcesize = source.getRealLineCount();
    boolean[] stmts = new boolean[sourcesize];
    for(int i = 0; i < sourcesize; i += 1) {
      int coverageStatus = decodedCoverge[i];
      if(coverageStatus== ICounter.FULLY_COVERED
          || coverageStatus == ICounter.PARTLY_COVERED) {
        stmts[i] = true;
      } else {
        stmts[i] = false;
      }
    }
    return stmts;
  }
  
  boolean[] findCoverableStmts(
      int[] coverage, LineCoverageFormat format, int length) {
    int[] decodedCoverge = null;
    if(format == LineCoverageFormat.DENSE) {
      decodedCoverge = decodeDense(coverage); 
    } else {
      decodedCoverge = decodeCompact(coverage);
    }
    
    boolean[] stmts = new boolean[length];
    for(int i = 0; i < length; i += 1) {
      int coverageStatus = decodedCoverge[i];
      if(coverageStatus == ICounter.EMPTY) {
        stmts[i] = false;
      } else {
        stmts[i] = true;
      }
    }
    return stmts;
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
