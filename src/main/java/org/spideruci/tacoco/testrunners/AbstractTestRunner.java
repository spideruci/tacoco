package org.spideruci.tacoco.testrunners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;



public abstract class AbstractTestRunner {
	
	public static enum TestType {JUNIT, TESTNG, UNIFIED, UNKNOWN};
	public static boolean LOGGING = false;

	
	public double testRunTime=0;
	public int executedTestCount=0;
	public int failedTestCount=0;
	public int ignoredTestCount=0;
	
	public abstract boolean shouldRun(Class<?> test);
	public abstract void listenThrough(ITacocoTestListener listener);
	public abstract Callable<AnalysisResults> getExecutableTest(Class<?> test);
	public abstract void printTestRunSummary(AnalysisResults results);
	
	public static AbstractTestRunner getInstance(final AbstractBuildProbe probe) {
		for(final String test : probe.getTestClasses()){
			try {
				switch(getTestType(Class.forName(test))){
				case JUNIT:
					return new JUnitRunner();
				case TESTNG:
					return new TestNGRunner();
				case UNIFIED:
					return new UnifiedTestRunner();
				case UNKNOWN:
					continue;
				}
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static TestType getTestType(final Class<?> test){
		
		if(test == null || Modifier.isAbstract(test.getModifiers())) {
			return TestType.UNKNOWN;
		}

		if (UnifiedTestRunner.containsExecutableTest(test)) {
			// We are going to give the UnifiedTestRunner first dibs.
			// If it is able to find any executable test, then we run with it
			// else, fall back on the individual test types
			return TestType.UNIFIED; // UnifiedTestRunner
		}
		
		if(junit.framework.TestCase.class.isAssignableFrom(test)) {
			return TestType.JUNIT; //JUnit3
		}

		for(final Method testMethod : test.getMethods()) {
			if(testMethod.getAnnotation(org.junit.Test.class) != null){
				return TestType.JUNIT; //JUnit4
			}
			else if(testMethod.getAnnotation(org.testng.annotations.Test.class) != null){
				return TestType.TESTNG; //TESTNG
			}
			else{
				return TestType.UNKNOWN;
			}

		}
		
		return TestType.UNKNOWN;
	}

}
