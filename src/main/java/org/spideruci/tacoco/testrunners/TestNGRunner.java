package org.spideruci.tacoco.testrunners;

import java.util.concurrent.Callable;

import org.spideruci.tacoco.analysis.AnalysisResults;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class TestNGRunner extends AbstractTestRunner {
	
	private final static String TESTNG_REPORT = "TESTNG_REPORT";
	public static boolean LOGGING = false;

	private Class<?> testClass;
	private TestNG testCore;
	
	@Override
	public void listenThrough(Object listener) {
		if(listener != null && listener instanceof ITestNGListener) {
			this.testCore.addListener(listener);
		} else {
			return;
		}
	}

	public TestNGRunner() {
		this.testClass = null;
		this.testCore = new TestNG();
	}
	
	@Override
	public void printTestRunSummary(AnalysisResults results) {
		System.out.println("testng.printTestRunSummary");
		
		for(ITestListener listener: this.testCore.getTestListeners()){
			if(listener instanceof TestListenerAdapter){
				TestListenerAdapter adapter = (TestListenerAdapter) listener;
				for(ITestResult result : adapter.getPassedTests()){
					System.out.println("Result: " + result);
				}
			}
		}
		/*
		result = results.get(TESTNG_REPORT);
		
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
		*/
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
		/*
		if(this.testClass == null) {
			return false;
		}
		
		if(Modifier.isAbstract(this.testClass.getModifiers())) {
			return false;
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
		*/
		
		return true;
	}
	
	@Override
	public Callable<AnalysisResults> getExecutableTest(final Class<?> test) {
		final TestNG core = this.testCore;
		return new Callable<AnalysisResults>() {
			
			@Override
			public AnalysisResults call() {
				try {
					core.setTestClasses(new Class[]{test});
					core.run();
					AnalysisResults results = new AnalysisResults();
					
					//results.put(JUNIT_TEST_RESULT, result);
					//results.put(TEST_CLASS_NAME, test.getName());
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
