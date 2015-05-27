package org.spideruci.tacoco.reporting;

import static org.spideruci.tacoco.reporting.data.SourceFileCoverageBuilder.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CoverageJsonPrinter implements ICoveragePrintable {
  
  private final IBundleCoverage coverage;
  private final PrintStream out;
  private final Gson gson;
  private final LineCoverageFormat format;
  
  public CoverageJsonPrinter(IBundleCoverage coverage, PrintStream out, 
      boolean pretty, LineCoverageFormat format) {
    this.coverage = coverage;
    this.out = out; 
    GsonBuilder gsonBuilder = new GsonBuilder();
    if(pretty) {
      gsonBuilder.setPrettyPrinting();
    }
    gson = gsonBuilder.create();
    this.format = format;
  }
  
  @Override
  public void printCoverageTitle() {
    out.println(coverage.getName());
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public void printCoverage() {
    
    ArrayList<ISourceFileCoverage> sourcefilesCovergae = 
        amassSourcefilesCoverage();
    SourceFileCoverage[] sourcefiles = coverage(format, sourcefilesCovergae);
    out.print(gson.toJson(sourcefiles));
  }
  
    @SuppressWarnings("rawtypes")
    private SourceFileCoverage[] coverage(LineCoverageFormat format, 
        ArrayList<ISourceFileCoverage> sourcefilesCoverage) {
      int sourcefileCount = sourcefilesCoverage.size();
      switch (format) {
      case LOOSE:
      {
        SourceFileCoverage[] sourcefiles = new SourceFileCoverage[sourcefileCount];
        int counter = 0;
        for(ISourceFileCoverage srcCov : sourcefilesCoverage) {
          sourcefiles[counter] = buildLoose(srcCov, coverage.getName());
          counter ++;
        }
        return sourcefiles;
        
      }
      case COMPACT:
      {
        SourceFileCoverage[] sourcefiles = new SourceFileCoverage[sourcefileCount];
        int counter = 0;
        for(ISourceFileCoverage srcCov : sourcefilesCoverage) {
          sourcefiles[counter] = buildCompact(srcCov, coverage.getName());
          counter ++;
        }
        return sourcefiles;
      }
      case DENSE:
      {
        SourceFileCoverage[] sourcefiles = new SourceFileCoverage[sourcefileCount];
        int counter = 0;
        for(ISourceFileCoverage srcCov : sourcefilesCoverage) {
          sourcefiles[counter] = buildDense(srcCov, coverage.getName());
          counter ++;
        }
        return sourcefiles;
      }
      default:
        throw new RuntimeException("Unknonw format! --- " + format.toString());
      }
    }
  
    private ArrayList<ISourceFileCoverage> amassSourcefilesCoverage() {
      ArrayList<ISourceFileCoverage> sourcefilesCoverage = new ArrayList<>();
      Collection<IPackageCoverage> packages = coverage.getPackages();
      for(IPackageCoverage packageCoverage : packages) {
        Collection<ISourceFileCoverage> sources = 
            packageCoverage.getSourceFiles();
        for(ISourceFileCoverage sourceCoverage : sources) {
          sourcefilesCoverage.add(sourceCoverage);
        }
      }
      return sourcefilesCoverage;
    }
}
