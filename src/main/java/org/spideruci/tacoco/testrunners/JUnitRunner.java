package org.spideruci.tacoco.testrunners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import junit.framework.TestCase;

import org.spideruci.tacoco.analysis.AnalysisResults;



public final class JUnitRunner extends AbstractTestRunner {
	
	public final static String JUNIT_TEST_RESULT = "test-result";
	public static boolean LOGGING = false;

	private Class<?> testClass;


	private JUnitCore testCore;
	
	public void listenThrough(RunListener listener) {
		if(listener != null) {
			this.testCore.addListener(listener);
		} else {
			return;
		}
	}

	public JUnitRunner() {
		this.testClass = null;
		this.testCore = new JUnitCore();
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Starting "+testClass);
			Result result = this.testCore.run(testClass);
			if(this.results == null) {
				this.results = new AnalysisResults();
			}
			
			this.results.put(JUNIT_TEST_RESULT, result);
			this.printTestRunSummary(); // TODO Move to AbstractRuntimeAnalyzer.runTests.
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void printTestRunSummary() {
		Result result = this.results.get(JUNIT_TEST_RESULT);
		testRunTime = result.getRunTime()/1000.0;
		executedTestCount = result.getRunCount();
		failedTestCount = result.getFailureCount();
		ignoredTestCount = result.getIgnoreCount();
		System.out.println("Finishing "+testClass
				+" Tests run: "+executedTestCount
				+" Failures: "+failedTestCount
				+" Errors: 0"   //TBD
				+" Skipped: "+ignoredTestCount
				+" Time elapsed: "+ testRunTime +"sec");
		if(this.failedTestCount !=0) {
			System.out.println("---------------------Failures--------------------");
			for(Failure f: result.getFailures()){
				System.out.println("Header: " + f.getTestHeader());
				System.out.println("Message: " + f.getMessage());
				System.out.println("Description: " + f.getDescription());
				System.out.println("Header: "+f.getTestHeader());
				System.out.println("Trace: "+f.getTrace());	
			}
		}
	}
	
	/**
	 * Checks to see if the given test-class should be executed as a Junit Test.
	 * @param testClass
	 * @return
	 * true if:<br/>
	 * <ul>
	 * 	<li>at least one method in {@code testClass} has a @Test annotation //JUnit4;
	 * 	<li>at least one method in {@code testClass} has the string `suite` in it;
	 * 	<li>{@code testClass} is a Junit3 TestCase class type;
	 * </ul>
	 * false - {@code testClass} is an abstract type, or if none of the above conditions are met:
	 * @throws NullPointerException 
	 * if this runner is not setup with a test-class with {@code setTestClass()}. 
	 */
	@Override
	public boolean shouldRun() {
		if(Modifier.isAbstract(this.testClass.getModifiers())) {
			return false;
		}
		
		if(TestCase.class.isAssignableFrom(this.testClass)) {
			return true;
		}

		for(Method testMethod : this.testClass.getMethods()){
			Test annotation = testMethod.getAnnotation(Test.class);
			if(annotation != null) {
				return true;
			}
			
			if(testMethod.getName().equals("suite")) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void setTest(Class<?> test) {
		if(test == null) {
			return;
		}
		this.testClass = test;
	}
}
