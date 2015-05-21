package org.spideruci.tacoco.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jacoco.core.analysis.ILine;
import org.spideruci.tacoco.reporting.data.CoverageMatrix;
import org.spideruci.tacoco.reporting.data.CoverageMatrix.SourceFile;
import org.spideruci.tacoco.reporting.data.LineCoverageCoder;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class CoverageJsonReader {
  private final File jsonFile;
  
  public CoverageJsonReader(final File json) {
    this.jsonFile = json;
  }

  @SuppressWarnings("rawtypes")
  public SourceFileCoverage[][] read() throws FileNotFoundException {
    SourceFileCoverage[][] covMatrix = null;
    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
    covMatrix = gson.fromJson(reader, SourceFileCoverage[][].class);
    return covMatrix;
  }
  
  @SuppressWarnings("rawtypes")
  public static void main(String[] args) throws FileNotFoundException {
    
    CoverageJsonReader reader = new CoverageJsonReader(new File(args[0]));
    SourceFileCoverage[][] data = reader.read();
    LineCoverageFormat covFormat = data[0][0].getFormat();
    LineCoverageCoder coder = new LineCoverageCoder();
    int testcount = data.length;
    CoverageMatrix covMat = new CoverageMatrix(covFormat, testcount); 

    int count = 0;
    for(SourceFileCoverage[] files : data) {
      String testCaseName = null;
      if((testCaseName = reader.getTestCaseName(files)) == null) {
        System.err.printf("Ignoring session#%d%n", count);
        count += 1;
        continue;
      }
      
      covMat.indexTestName(testCaseName);
      
      System.out.println(files.length);
      ArrayList<Integer> lineCoverages = new ArrayList<>();
      for(SourceFileCoverage file : files) {
        covMat.indexSourceFile(new SourceFile(file.getPackageName() + file.getFileName(), file.getFirstLine(), file.getLastLine()));
        System.out.println(file.getFileName() + "\t" + file.getLastLine());
        for(Object line : file.getLinesCoverage()) {
          if(covFormat == LineCoverageFormat.LOOSE) {
            int linecov = coder.encode((ILine)line);
            lineCoverages.add(linecov);
          } else {
            int lineIntValue = reader.readDoubleObjectAsInt(line);
            lineCoverages.add(lineIntValue);
          }
        }
        int[] codedLineCoverage = new int[lineCoverages.size()];
        for(int i = 0; i < codedLineCoverage.length; i += 1) {
          codedLineCoverage[i] = lineCoverages.get(i);
        }
        covMat.addStmtCoverage(testCaseName, codedLineCoverage);
        
      }
      System.out.println();
    }
    
    covMat.printMatrix();
  }
  
  int readDoubleObjectAsInt(Object line) {
    Double lineDoubleValue = (Double) line; 
    long lineLongValue = lineDoubleValue.longValue();
    int lineIntValue = (int) lineLongValue;
    return lineIntValue;
  }
  
  @SuppressWarnings({"rawtypes"})
  private String getTestCaseName(SourceFileCoverage[] files) {
    
    String testCase = files[0].getSessionName();
    
    for(int i = 1; i <= files.length - 1; i += 1) {
      String nextTestCase = files[i].getSessionName();
      if(!testCase.equals(nextTestCase)) return null;
    }
    
    return testCase;
  }
  
}
