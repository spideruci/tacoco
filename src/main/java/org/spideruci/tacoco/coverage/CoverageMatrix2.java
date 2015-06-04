package org.spideruci.tacoco.coverage;

import java.io.PrintStream;
import java.util.HashMap;

import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CoverageMatrix2 {
  
  private final HashMap<String, Integer> testNameIndex;
  private final HashMap<SourceFile, SourceSpecificCoverageMatrix> sourceFileIndex;
  private final LineCoverageFormat format;
  
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
  
  public void dumpMatrix(PrintStream out, boolean shouldPrettyPrint) {
    int testCount = this.testNameIndex.size();
    String[] testCases = new String[testCount];
    
    for(String testCaseName : testNameIndex.keySet()) {
      if(testCaseName == null || testCaseName.isEmpty()) continue;
      int index = testNameIndex.get(testCaseName);
      testCases[index] = testCaseName;
    }
    
    Gson testGson = getGson(shouldPrettyPrint);
    String testCasesJson = testGson.toJson(testCases);
    testGson = null;
    
    out.print('{');
    out.println("\"testCount\":" + testCount + ",");
    out.println("\"testsIndex\":" + testCasesJson + ",");
    
    out.print("\"sources\":[");
    int totalSourceUnits = sourceFileIndex.keySet().size();
    int count = 0;
    for(SourceFile sourceUnit : sourceFileIndex.keySet()) {
      SourceSpecificCoverageMatrix coverage = sourceFileIndex.get(sourceUnit);
      System.out.println(coverage.getSourceName());
      System.out.println(coverage.getActivatingTestCount());
      Gson gson = getGson(shouldPrettyPrint);
      String json = gson.toJson(coverage);
      gson = null;
      out.print(json);
      count += 1;
      if(count < totalSourceUnits) out.println(',');
    }
    out.print("]}");
  }
  
    private Gson getGson(boolean isPretty) {
      Gson gson;
      if(isPretty) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
      } else {
        gson = new Gson();
      }
      return gson;
    }
}
