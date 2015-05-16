package org.spideruci.tacoco.reporting;

import java.io.PrintStream;
import java.util.Collection;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

public class CoveragePrettyPrinter implements ICoveragePrintable {
  
  private final IBundleCoverage coverage;
  private final PrintStream out;
  
  public CoveragePrettyPrinter(IBundleCoverage coverage, PrintStream out) {
    this.coverage = coverage;
    this.out = out; 
  }
  
  @Override
  public void printCoverageTitle() {
    out.println(coverage.getName());
  }
  
  @Override
  public void printCoverage() {
    Collection<IPackageCoverage> packages = coverage.getPackages();
    for(IPackageCoverage packageCoverage : packages) {
      out.println(packageCoverage.getName() + 
          " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
      Collection<ISourceFileCoverage> sources = packageCoverage.getSourceFiles();
      for(ISourceFileCoverage sourceCoverage : sources) {
        prettyPrintSourceLines(sourceCoverage);
      }
    }
  }
  
    private void prettyPrintSourceLines(ISourceFileCoverage sourceCoverage) {
      out.println("\t" + sourceCoverage.getName() + 
          " ... " + prettyCoverageCount(sourceCoverage.getClassCounter()));
      int firstLine = sourceCoverage.getFirstLine();
      int lastLine = sourceCoverage.getLastLine();
      
      for(int line = firstLine; line <= lastLine; line += 1) {
        if(line == -1) continue;
        ILine sourceLine = sourceCoverage.getLine(line);
        if(sourceLine == null) {
          out.printf("%d: null\n", line);
          continue;
        }
        out.printf("%d: %s, %s, %s\n",
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
    
    public void printPackageCoverage() {
      Collection<IPackageCoverage> packages = coverage.getPackages();
      for(IPackageCoverage packageCoverage : packages) {
        out.println(packageCoverage.getName() + 
            " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
      }
    }
    
    public void printClassCoverage() {
      Collection<IPackageCoverage> packages = coverage.getPackages();
      for(IPackageCoverage packageCoverage : packages) {
        out.println(packageCoverage.getName() + 
            " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
        Collection<IClassCoverage> classes = packageCoverage.getClasses();
        for(IClassCoverage classCoverage : classes) {
          out.println("\t" + classCoverage.getName() + 
              " ... " + prettyCoverageCount(classCoverage.getClassCounter()));
        }
      }
    }
    
    public void printSourceFileCoverage() {
      Collection<IPackageCoverage> packages = coverage.getPackages();
      for(IPackageCoverage packageCoverage : packages) {
        out.println(packageCoverage.getName() + 
            " ... " + prettyCoverageCount(packageCoverage.getClassCounter()));
        Collection<ISourceFileCoverage> sources = packageCoverage.getSourceFiles();
        for(ISourceFileCoverage sourceCoverage : sources) {
          out.println("\t" + sourceCoverage.getName() + 
              " ... " + prettyCoverageCount(sourceCoverage.getClassCounter()));
        }
      }
    }

}
