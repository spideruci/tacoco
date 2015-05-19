package org.spideruci.tacoco.reporting;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;


public class ExecutionDataParser implements IExecutionDataVisitor {
  
  private final File classesDirectory;
  private String coverageTitle;
  private final ArrayList<IBundleCoverage> coverageBundles;
  private ExecutionDataStore execDataStore = new ExecutionDataStore();
  
  public ExecutionDataParser(final File projectDirectory) {
    this.coverageTitle = projectDirectory.getName();
    this.classesDirectory = new File(projectDirectory, "target/classes");
    coverageBundles = new ArrayList<>();
  }

  public void visitClassExecution(final ExecutionData data) {
    if(data == null) return;
    System.out.printf("adding exec-data for: %s %d%n", 
        data.getName(), 
        getHitCount(data.getProbes()));
    execDataStore.put(data);
    
  }
  
  public void resetExecDataStore() {
    if(execDataStore.getContents().size() == 0) {
      execDataStore = new ExecutionDataStore();
      return;
    }
    
    try {
      System.out.printf("analyzing exec-data for: %s%n", coverageTitle);
      IBundleCoverage coverage = this.analyzeStructure(execDataStore);
      coverageBundles.add(coverage);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    execDataStore = new ExecutionDataStore();
  }
  
  public void setCoverageTitle(final String title) {
    this.coverageTitle = title;
  }
  
  public ArrayList<IBundleCoverage> getCoverageBundles() {
    return coverageBundles;
  }
  
  private IBundleCoverage analyzeStructure(final ExecutionDataStore data) throws IOException {
    final CoverageBuilder coverageBuilder = new CoverageBuilder();
    
    final Analyzer analyzer = new Analyzer(data, coverageBuilder);

    analyzer.analyzeAll(classesDirectory);

    return coverageBuilder.getBundle(coverageTitle);
  }
  
  private int getHitCount(final boolean[] data) {
    int count = 0;
    for (final boolean hit : data) {
      if (hit) {
        count++;
      }
    }
    return count;
  }

}
