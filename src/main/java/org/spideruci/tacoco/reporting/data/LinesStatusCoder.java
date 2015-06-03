package org.spideruci.tacoco.reporting.data;

import java.util.Collection;

import org.jacoco.core.analysis.ILine;

public class LinesStatusCoder {
  
  private final static int SHIFT = 2; 
  private final static int MASK = 0b11;
  
  public int[] encode(Collection<ILine> linesCoverage) {
    int lineCount = linesCoverage.size();
    int codeCount = (lineCount / 16) + (lineCount % 16 == 0 ? 0 : 1);
    int[] codedCoverage = new int[codeCount];
    
    int code = 0;
    int count = 0;
    int codeIndex = 0;
    for(ILine lineCoverage : linesCoverage) {
      int status = lineCoverage.getStatus();
      code = code | status;
      count += 1;
      if(count == lineCount) {
        code = code << (SHIFT * (16 - (count % 16)));
        codedCoverage[codeIndex] = code;
        break;
      }
      
      if(count % 16 == 0) {
        codedCoverage[codeIndex] = code;
        codeIndex += 1;
        code = 0;
        continue;
      }
      
      code = code << SHIFT;
    }
    
    return codedCoverage;
  }
  
  public int[] decode(int[] codedCoverage) {
    int statusCount = codedCoverage.length*16;
    int index = statusCount - 1;
    int[] statuses = new int[statusCount];
    for(int i = codedCoverage.length - 1; i >= 0; i -= 1) {
      int code = codedCoverage[i];
      for(int j = 1; j <= 16; j += 1) {
        int status = code & MASK;        
        statuses[index] = status;
        index -= 1;
        code = code >>> SHIFT;
      }
    }
    
    return statuses;
  }

}
