package org.spideruci.tacoco.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jacoco.core.analysis.ILine;
import org.spideruci.tacoco.reporting.data.CoverageMatrix;
import org.spideruci.tacoco.reporting.data.CoverageMatrix.SourceFile;
import org.spideruci.tacoco.reporting.data.LineCoverageCoder;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class CoverageJsonReader {
  
  public static void main(String[] args) throws IOException {
    File jsonFile = new File(args[0]);
    LineCoverageFormat covFormat = readCoverageFormat(jsonFile);
    InputStreamReader jsonIn = 
        new InputStreamReader(new FileInputStream(jsonFile));
    JsonReader jsonreader = new JsonReader(jsonIn);
    CoverageJsonReader reader = new CoverageJsonReader(jsonreader);;
    CoverageMatrix covMat = reader.read(covFormat);
    covMat.printMatrix();
    covMat.dumpMatrix();
  }
  
  private final JsonReader jsonReader;
  private final Gson gson;
  
  public CoverageJsonReader(final JsonReader reader) {
    this.jsonReader = reader;
    this.gson = new Gson();
  }
  
  public static LineCoverageFormat readCoverageFormat(final File file) 
      throws IOException {
    InputStreamReader jsonIn = new InputStreamReader(new FileInputStream(file));
    JsonReader jsonReader = new JsonReader(jsonIn);
    jsonReader.beginArray();
    jsonReader.beginArray();
    Gson gson = new Gson();
    @SuppressWarnings("rawtypes") SourceFileCoverage 
    sourceFileCoverage = gson.fromJson(jsonReader, SourceFileCoverage.class);
    jsonReader.close();
    return sourceFileCoverage.getFormat();
  } 
  
  public void startReading() throws IOException {
    jsonReader.beginArray();
  }
  
  public void endReading() throws IOException {
    jsonReader.endArray();
    jsonReader.close();
  }
  
  public boolean hasNext() throws IOException {
    return jsonReader.hasNext();
  }
  
  public void startReadingTestCase() throws IOException {
    jsonReader.beginArray();
  }
  
  public void endReadingTestCase() throws IOException {
    jsonReader.endArray();
  }
  
  public <T> SourceFileCoverage<T> readNextSourceFileCoverage() 
      throws JsonIOException, JsonSyntaxException, IOException {
    if(jsonReader.hasNext()) {
      Class<?> type = new SourceFileCoverage<T>().getClass();
      SourceFileCoverage<T> sourcefileCov = gson.fromJson(jsonReader, type);
      return sourcefileCov;
    } else {
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  public CoverageMatrix read(LineCoverageFormat covFormat) throws IOException {
    int count = 0;
    LineCoverageCoder coder = new LineCoverageCoder();
    CoverageMatrix covMat = new CoverageMatrix(covFormat); 
    
    this.startReading();
    while(this.hasNext()) {
      String testCaseName = null;
      ArrayList<Integer> lineCoverages = new ArrayList<>();
      this.startReadingTestCase();
      while(this.hasNext()) {
        SourceFileCoverage sourcefile = this.readNextSourceFileCoverage();
        assert sourcefile != null; // hasNext() should have taken care of this.
        String currTestCaseName = sourcefile.getSessionName();
        if(testCaseName == null) {
          testCaseName = currTestCaseName;
          covMat.indexTestName(testCaseName);
        } else {
          if(!testCaseName.equals(currTestCaseName)) {
            System.err.printf("Ignoring session#%d%n", count);
            lineCoverages = null;
            count += 1;
            break;
          }
        }
        
        String filename = sourcefile.getPackageName() + sourcefile.getFileName();
        int firstLine = sourcefile.getFirstLine();
        int lastLine = sourcefile.getLastLine();
        SourceFile source = new SourceFile(filename, firstLine, lastLine);
        covMat.indexSourceFile(source);
        
        System.out.printf("%s\t%s%n", sourcefile.getFileName(), 
            sourcefile.getLastLine());
        
        for(Object line : sourcefile.getLinesCoverage()) {
          if(covFormat == LineCoverageFormat.LOOSE) {
            Map insnCounter = (Map) ((Map)line).get("instructions");
            int ic = readDoubleObjectAsInt(insnCounter.get("covered"));
            int im = readDoubleObjectAsInt(insnCounter.get("missed"));
            Map branchCounter = (Map) ((Map)line).get("branches");
            int bc = readDoubleObjectAsInt(branchCounter.get("covered"));
            int bm = readDoubleObjectAsInt(branchCounter.get("missed"));
            int linecov = coder.encode(ic, im, bc, bm);
            lineCoverages.add(linecov);
          } else {
            int lineIntValue = this.readDoubleObjectAsInt(line);
            lineCoverages.add(lineIntValue);
          }
        }
        
      }
      this.endReadingTestCase();
      System.out.println();
      if(lineCoverages == null) continue;
      
      int[] codedLineCoverage = new int[lineCoverages.size()];
      for(int i = 0; i < codedLineCoverage.length; i += 1) {
        codedLineCoverage[i] = lineCoverages.get(i);
      }
      covMat.addStmtCoverage(testCaseName, codedLineCoverage);
    }
    
    return covMat;
  }
  
  int readDoubleObjectAsInt(Object line) {
    Double lineDoubleValue = (Double) line; 
    long lineLongValue = lineDoubleValue.longValue();
    int lineIntValue = (int) lineLongValue;
    return lineIntValue;
  }  
}
