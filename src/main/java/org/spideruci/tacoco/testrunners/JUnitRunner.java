package org.spideruci.tacoco.testrunners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;
import org.spideruci.tacoco.testlisteners.JUnitListenerAdapter;

import junit.framework.TestCase;



public final class JUnitRunner extends AbstractTestRunner {
	
	private final static String JUNIT_TEST_RESULT = "test-result";
	private static final String TEST_CLASS_NAME = "test-class-name";
	
	private JUnitCore testCore;
	
	public void listenThrough(ITacocoTestListener listener) {
		JUnitListenerAdapter adapter = new JUnitListenerAdapter(listener);
		this.testCore.addListener(adapter);
	}

	public JUnitRunner() {
		this.testCore = new JUnitCore();
	}
	
	@Override
	public void printTestRunSummary(AnalysisResults results) {
		Result result = results.get(JUNIT_TEST_RESULT);
		this.testRunTime = result.getRunTime()/1000.0;
		this.executedTestCount = result.getRunCount();
		this.failedTestCount = result.getFailureCount();
		this.ignoredTestCount = result.getIgnoreCount();
		String testName = results.get(TEST_CLASS_NAME);
		
		System.out.println("Finishing "+ testName
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
	public boolean shouldRun(Class<?> test) {
		if(test == null) {
			return false;
		}
		
		if(Modifier.isAbstract(test.getModifiers())) {
			return false;
		}
		
		if(TestCase.class.isAssignableFrom(test)) {
			return true;
		}

		for(Method testMethod : test.getMethods()) {
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
	public Callable<AnalysisResults> getExecutableTest(final Class<?> test) {
		final JUnitCore core = this.testCore;
		return new Callable<AnalysisResults>() {
			
			@Override
			public AnalysisResults call() {
				try {
					//System.out.println("Starting "+test);
					Result result = core.run(test);
					AnalysisResults results = new AnalysisResults();
					
					results.put(JUNIT_TEST_RESULT, result);
					results.put(TEST_CLASS_NAME, test.getName());
					return results;
//					this.printTestRunSummary(); // TODO Move to AbstractRuntimeAnalyzer.runTests.
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				
			}
		};
	}
}
