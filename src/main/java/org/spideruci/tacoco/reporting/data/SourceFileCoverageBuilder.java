package org.spideruci.tacoco.reporting.data;

import java.util.ArrayList;
import java.util.Collection;

import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

public class SourceFileCoverageBuilder {
  
  public static SourceFileCoverage buildCompact(ISourceFileCoverage coverage) {
    String packageName = coverage.getPackageName();
    String sourcefileName = coverage.getName();
    int firstLine = coverage.getFirstLine();
    int lastLine = coverage.getLastLine();
    int[] linesCoverage = null;
    if(firstLine != -1) {
      linesCoverage = new int[lastLine - firstLine + 1];
      LineCoverageCoder coder = new LineCoverageCoder();
      int counter = 0;
      for(int i = firstLine; i <= lastLine; i += 1) {
        int codedCoverage = coder.encode(coverage.getLine(i)); 
        linesCoverage[counter] = codedCoverage;
        counter += 1;
      }
    }
    
    SourceFileCoverage cov = new SourceFileCoverage(sourcefileName, 
        packageName, firstLine, LineCoverageFormat.COMPACT, linesCoverage);
    return cov;
  }
  
  public static SourceFileCoverage buildDense(ISourceFileCoverage coverage) {
    String packageName = coverage.getPackageName();
    String sourcefileName = coverage.getName();
    int firstLine = coverage.getFirstLine();
    int lastLine = coverage.getLastLine();
    
    Collection<ILine> linesCoverage = new ArrayList<>();
    if(firstLine != -1) {
      for(int i = firstLine; i <= lastLine; i += 1) {
        linesCoverage.add(coverage.getLine(i));
      }      
    }
    
    LinesStatusCoder coder = new LinesStatusCoder();
    int[] lineStatuses = coder.encode(linesCoverage);
    SourceFileCoverage cov = new SourceFileCoverage(sourcefileName, 
        packageName, firstLine, LineCoverageFormat.DENSE, lineStatuses);
    return cov;
  }
  
  

}
