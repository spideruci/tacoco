package org.spideruci.tacoco.testrunners;

import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.probe.AbstractBuildProbe;

public abstract class AbstractTestRunner implements Runnable {
	
	public double testRunTime=0;
	public int executedTestCount=0;
	public int failedTestCount=0;
	public int ignoredTestCount=0;
	
	protected AnalysisResults results;
	
	public abstract boolean shouldRun();
	public abstract void listenThrough(RunListener listener);
	public abstract void setTest(Class<?> test);
	
	protected abstract void printTestRunSummary();
	
	public static AbstractTestRunner getInstance(AbstractBuildProbe probe) {
		return new JUnitRunner();
	}

}
