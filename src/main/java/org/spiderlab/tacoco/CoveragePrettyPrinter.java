package org.spiderlab.tacoco;

import java.util.Collection;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

public class CoveragePrettyPrinter {
  
  private final IBundleCoverage coverage;
  
  public CoveragePrettyPrinter(IBundleCoverage coverage) {
    this.coverage = coverage;
  }
  
  public void printCoverageTitle() {
    System.out.println(coverage.getName());
  }
  
  public void printPackageCoverage() {
    Collection<IPackageCoverage> packages = coverage.getPackages();
    for(IPackageCoverage packageCoverage : packages) {
      System.out.println(packageCoverage.getName() + 
          " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
    }
  }
  
  public void printClassCoverage() {
    Collection<IPackageCoverage> packages = coverage.getPackages();
    for(IPackageCoverage packageCoverage : packages) {
      System.out.println(packageCoverage.getName() + 
          " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
      Collection<IClassCoverage> classes = packageCoverage.getClasses();
      for(IClassCoverage classCoverage : classes) {
        System.out.println("\t" + classCoverage.getName() + 
            " ... " + prettyCoverageCount(classCoverage.getClassCounter()));
      }
    }
  }
  
  public void printSourceFileCoverage() {
    Collection<IPackageCoverage> packages = coverage.getPackages();
    for(IPackageCoverage packageCoverage : packages) {
      System.out.println(packageCoverage.getName() + 
          " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
      Collection<ISourceFileCoverage> sources = packageCoverage.getSourceFiles();
      for(ISourceFileCoverage sourceCoverage : sources) {
        System.out.println("\t" + sourceCoverage.getName() + 
            " ... " + prettyCoverageCount(sourceCoverage.getClassCounter()));
      }
    }
  }
  
  public void printSourceLineCoverage() {
    Collection<IPackageCoverage> packages = coverage.getPackages();
    for(IPackageCoverage packageCoverage : packages) {
      System.out.println(packageCoverage.getName() + 
          " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
      Collection<ISourceFileCoverage> sources = packageCoverage.getSourceFiles();
      for(ISourceFileCoverage sourceCoverage : sources) {
        prettyPrintSourceLines(sourceCoverage);
      }
    }
  }
  
    private void prettyPrintSourceLines(ISourceFileCoverage sourceCoverage) {
      System.out.println("\t" + sourceCoverage.getName() + 
          " ... " + prettyCoverageCount(sourceCoverage.getClassCounter()));
      int firstLine = sourceCoverage.getFirstLine();
      int lastLine = sourceCoverage.getLastLine();
      
      for(int line = firstLine; line <= lastLine; line += 1) {
        if(line == -1) continue;
        ILine sourceLine = sourceCoverage.getLine(line);
        if(sourceLine == null) {
          System.out.printf("%d: null\n", line);
          continue;
        }
        System.out.printf("%d: %s, %s, %s\n",
            line,
            lineStatusString(sourceLine.getStatus()),
            prettyCoverageCount(sourceLine.getInstructionCounter()),
            prettyCoverageCount(sourceLine.getBranchCounter()));
      }
    }
  
    private String prettyCoverageCount(ICounter counter) {
      int covered = counter.getCoveredCount();
      int total = counter.getTotalCount();
      return covered +  " of " + total;
    }
    
    private String lineStatusString(int lineStatus) {
      switch(lineStatus) {
      case ICounter.EMPTY: return "EMPTY";
      case ICounter.NOT_COVERED: return "NOT COVERED";
      case ICounter.PARTLY_COVERED: return "PARTLY COVERED";
      case ICounter.FULLY_COVERED: return "FULLY COVERED";
      default: return "UNKNOWN STATUS";
      }
    }
    


}
