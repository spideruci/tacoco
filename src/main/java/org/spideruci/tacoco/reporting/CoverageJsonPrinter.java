package org.spideruci.tacoco.reporting;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CoverageJsonPrinter implements ICoveragePrintable {
  
  private final IBundleCoverage coverage;
  private final PrintStream out;
  
  public CoverageJsonPrinter(IBundleCoverage coverage, PrintStream out) {
    this.coverage = coverage;
    this.out = out; 
  }
  
  @Override
  public void printCoverageTitle() {
    out.println(coverage.getName());
  }
  
  @Override
  public void printCoverage() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ArrayList<ISourceFileCoverage> sourcefilesCovergae = 
        amassSourcefilesCoverage();
    int sourcefileCount = sourcefilesCovergae.size();
    ISourceFileCoverage[] sourcefiles = 
        sourcefilesCovergae.toArray(new ISourceFileCoverage[sourcefileCount]);
    out.print(gson.toJson(sourcefiles));
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
