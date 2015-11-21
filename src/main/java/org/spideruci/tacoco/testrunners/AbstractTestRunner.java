package org.spideruci.tacoco.testrunners;

import java.util.concurrent.Callable;

import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.probe.AbstractBuildProbe;

public abstract class AbstractTestRunner {
	
	public static enum TestType {JUNIT, TESTNG, UNKNOWN};
	
	public double testRunTime=0;
	public int executedTestCount=0;
	public int failedTestCount=0;
	public int ignoredTestCount=0;
	
	public abstract boolean shouldRun(Class<?> test);
	public abstract void listenThrough(Object listener);
	public abstract Callable<AnalysisResults> getExecutableTest(Class<?> test);
	public abstract void printTestRunSummary(AnalysisResults results);
	
	public static AbstractTestRunner getInstance(AbstractBuildProbe probe) {
		for(String test : probe.getTestClasses()){
			try {
				switch(getTestType(Class.forName(test))){
				case JUNIT:
					return new JUnitRunner();
				case TESTNG:
					return new TestNGRunner();
				case UNKNOWN:
					continue;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static TestType getTestType(Class<?> test){
		return TestType.TESTNG;
	}

}
