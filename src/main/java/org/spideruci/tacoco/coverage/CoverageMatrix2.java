package org.spideruci.tacoco.coverage;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.jacoco.core.analysis.ICounter;
import org.spideruci.tacoco.reporting.data.LineCoverageCoder;
import org.spideruci.tacoco.reporting.data.LinesStatusCoder;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;

public class CoverageMatrix2 {
  
  private final HashMap<String, Integer> testNameIndex;
  private final HashMap<SourceFile, SourceSpecificCoverageMatrix> sourceFileIndex;
  private final LineCoverageFormat format;
  private int testCount;
  
  public CoverageMatrix2(LineCoverageFormat fmt) {
    testNameIndex = new HashMap<>();
    sourceFileIndex = new HashMap<>();
    format = fmt;
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
   * @return the format
   */
  public LineCoverageFormat getFormat() {
    return format;
  }
  
  public void setTestCount(int testCount) {
    this.testCount = testCount;
  }
  
  public int getTestCount() {
    return testCount;
  }
  
  public void addStmtCoverage(SourceFile source, String testName, int[] coverage) {
    SourceSpecificCoverageMatrix srcCoverage = sourceFileIndex.get(source);
    boolean findCoverableLines = false;
    if(srcCoverage == null) {
      srcCoverage = new SourceSpecificCoverageMatrix(source);
      sourceFileIndex.put(source, srcCoverage);
      findCoverableLines = true;
    }
    
    int testIndex = testNameIndex.get(testName);
    srcCoverage.addStmtCoverage(testIndex, coverage, this.format, findCoverableLines);
  }
  
  public void dumpMatrix(PrintStream out) {
    Gson gson = new Gson();
    for(SourceFile sourceUnit : sourceFileIndex.keySet()) {
      SourceSpecificCoverageMatrix coverage = sourceFileIndex.get(sourceUnit);
      System.out.println(coverage.getSourceName());
      System.out.println(coverage.getActivatingTestCount());
      String json = gson.toJson(coverage);
      out.println(json);
    }
    
    
  }
}
