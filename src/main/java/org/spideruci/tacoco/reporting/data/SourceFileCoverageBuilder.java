package org.spideruci.tacoco.reporting.data;

import java.util.ArrayList;
import java.util.Collection;

import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

public class SourceFileCoverageBuilder {

  public static 
  SourceFileCoverage<ILine> buildLoose(ISourceFileCoverage coverage, 
      String sessionName) {
    String packageName = coverage.getPackageName();
    String sourcefileName = coverage.getName();
    int firstLine = coverage.getFirstLine();
    int lastLine = coverage.getLastLine();
    ILine[] linesCoverage = null;
    if(firstLine != -1) {
      linesCoverage = new ILine[lastLine - firstLine + 1];
      int counter = 0;
      for(int i = firstLine; i <= lastLine; i += 1) {
        linesCoverage[counter] = coverage.getLine(i);
        counter += 1;
      }
    }
    
    SourceFileCoverage<ILine> cov = 
        new SourceFileCoverage<>(sourcefileName, packageName, sessionName, 
            firstLine, lastLine, LineCoverageFormat.LOOSE, linesCoverage);
    return cov;
  }
  
  public static 
  SourceFileCoverage<Integer> buildCompact(ISourceFileCoverage coverage, 
      String sessionName) {
    String packageName = coverage.getPackageName();
    String sourcefileName = coverage.getName();
    int firstLine = coverage.getFirstLine();
    int lastLine = coverage.getLastLine();
    Integer[] linesCoverage = null;
    if(firstLine != -1) {
      linesCoverage = new Integer[lastLine - firstLine + 1];
      LineCoverageCoder coder = new LineCoverageCoder();
      int counter = 0;
      for(int i = firstLine; i <= lastLine; i += 1) {
        int codedCoverage = coder.encode(coverage.getLine(i)); 
        linesCoverage[counter] = codedCoverage;
        counter += 1;
      }
    }
    
    SourceFileCoverage<Integer> cov = 
        new SourceFileCoverage<>(sourcefileName, packageName, sessionName, 
            firstLine, lastLine, LineCoverageFormat.COMPACT, linesCoverage);
    return cov;
  }
  
  public static 
  SourceFileCoverage<Integer> buildDense(ISourceFileCoverage coverage,
      String sessionName) {
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
    int[] statuses = coder.encode(linesCoverage);
    Integer[] lineStatuses = new Integer[statuses.length];
    int count = 0;
    for(int status : statuses) {
      lineStatuses[count++] = status;
    }
    
    SourceFileCoverage<Integer> cov = 
        new SourceFileCoverage<>(sourcefileName, packageName, sessionName, 
            firstLine, lastLine, LineCoverageFormat.DENSE, lineStatuses);
    return cov;
  }
}
