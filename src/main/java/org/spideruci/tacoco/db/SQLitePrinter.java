package org.spideruci.tacoco.db;

import static org.spideruci.tacoco.reporting.data.SourceFileCoverageBuilder.buildCompact;
import static org.spideruci.tacoco.reporting.data.SourceFileCoverageBuilder.buildDense;
import static org.spideruci.tacoco.reporting.data.SourceFileCoverageBuilder.buildLoose;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.spideruci.tacoco.reporting.ICoveragePrintable;
import org.spideruci.tacoco.reporting.data.LineCoverageCoder;
import org.spideruci.tacoco.reporting.data.LinesStatusCoder;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.emory.mathcs.backport.java.util.Arrays;

public class SQLitePrinter implements ICoveragePrintable {
  
  private final IBundleCoverage coverage;
  private final Gson gson;
  private final DBUtil db;
  private final int projectID; 
  private boolean updateSourceInfo;
  
  public SQLitePrinter(IBundleCoverage coverage, 
		  DBUtil dbUtil, int projectID, boolean updateSourceInfo) {
    this.coverage = coverage;
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setPrettyPrinting();
    gson = gsonBuilder.create();
    this.db = dbUtil;
    this.projectID = projectID;
    this.updateSourceInfo = updateSourceInfo;
   
  }
  
  @Override
  public void printCoverageTitle() {
    System.out.println(coverage.getName());
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public void printCoverage() {
    
    ArrayList<ISourceFileCoverage> sourcefilesCovergae = 
        amassSourcefilesCoverage();
    SourceFileCoverage[] sourcefiles = coverage(sourcefilesCovergae);
    
    
    for(SourceFileCoverage<Integer> sourcefile: sourcefiles){
    	String sourceFQN = sourcefile.getPackageName()+"."+sourcefile.getFileName();
    	Integer[] encodedLinesStatuses = sourcefile.getLinesCoverage();
    	
    	int[] encodedStatuses = new int[encodedLinesStatuses.length];
    	for(int i = 0; i < encodedLinesStatuses.length; i += 1) {
    		encodedStatuses[i] = encodedLinesStatuses[i];
    	}
    	LinesStatusCoder coder = new LinesStatusCoder();
    	int[] decodedStatuses = coder.decode(encodedStatuses);
    	int fl = sourcefile.getFirstLine();
    	int ll = sourcefile.getLastLine();
    	int emptyLineCnt=0;
    	
    	String testFQN = sourcefile.getSessionName();
    	if(testFQN.endsWith("_F")) testFQN = testFQN.replaceAll("_F", "");
    	int sourceID = db.getSourceID(sourceFQN);
    	int testID = db.getTestID(testFQN);
    	
    	for(int i=0; i<ll-fl+1; ++i){
    		int lineNumber = i+fl;
    		switch(decodedStatuses[i]){
    		case ICounter.EMPTY:
    			++emptyLineCnt;break;
    		case ICounter.FULLY_COVERED:
    		case ICounter.PARTLY_COVERED:
    			db.insertLineCoverage(testID, sourceID, lineNumber, projectID);
    			break;
    		case ICounter.NOT_COVERED:
    			break;
    		}
    	}
    	if(updateSourceInfo){
    		db.insertSource(sourceFQN, (ll-fl-emptyLineCnt+1), projectID);
    	}
    }
  }
  
    @SuppressWarnings("rawtypes")
    private SourceFileCoverage[] coverage( 
        ArrayList<ISourceFileCoverage> sourcefilesCoverage) {
      int sourcefileCount = sourcefilesCoverage.size();
        SourceFileCoverage[] sourcefiles = new SourceFileCoverage[sourcefileCount];
        int counter = 0;
        for(ISourceFileCoverage srcCov : sourcefilesCoverage) {
          sourcefiles[counter] = buildDense(srcCov, coverage.getName());
          counter ++;
        }
        return sourcefiles;
      
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

	public void setUpdateSourceInfo(boolean b) {
		updateSourceInfo = b;
	}
}
