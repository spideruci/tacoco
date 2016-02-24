package org.spideruci.tacoco.testrunners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;



public abstract class AbstractTestRunner {
	
	public static enum TestType {JUNIT, TESTNG, UNKNOWN};
	public static boolean LOGGING = false;

	
	public double testRunTime=0;
	public int executedTestCount=0;
	public int failedTestCount=0;
	public int ignoredTestCount=0;
	
	public abstract boolean shouldRun(Class<?> test);
	public abstract void listenThrough(ITacocoTestListener listener);
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
		
		if(test == null) {
			return TestType.UNKNOWN;
		}
		
		if(Modifier.isAbstract(test.getModifiers())) {
			return TestType.UNKNOWN;
		}
		
		if(junit.framework.TestCase.class.isAssignableFrom(test)) {
			return TestType.JUNIT; //JUnit3
		}

		for(Method testMethod : test.getMethods()) {
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
