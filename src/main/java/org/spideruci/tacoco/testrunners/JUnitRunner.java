package org.spideruci.tacoco.testrunners;

import static org.spideruci.tacoco.cli.AbstractCli.LOG;
import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.AbstractCli.THREAD;
import static org.spideruci.tacoco.cli.AbstractCli.LISTENER;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.AbstractCli.readBooleanArgument;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.AbstractBuildProbe;
import org.spideruci.tacoco.testlisteners.JunitJacocoListener;

import junit.framework.TestCase;

public final class JUnitRunner extends AbstractTestRunner {

	private Class<?> testClass;
	private static JUnitCore core = new JUnitCore();
	static {
		//		core.addListener(new JunitJacocoListener());
	}

	public static boolean LOGGING = false;

	private JUnitCore testCore;
	
	public void listenThrough(RunListener listener) {
		if(listener != null) {
			this.testCore.addListener(listener);
		} else {
			return;
		}
	}

	public static void addListener() {
		String listenerClassName = readOptionalArgumentValue(LISTENER, null);
		if(listenerClassName == null) {
			return;
		}

		try {
			Class<?> listenerClass = Class.forName(listenerClassName);
			if(listenerClass == null) {
				return;
			}

			if(RunListener.class.isAssignableFrom(listenerClass)) {
				RunListener listener = (RunListener) listenerClass.newInstance();
				if(listener == null) {
					return;
				}
				core.addListener(listener);
			} else {
				return;
			}
		} catch (ClassNotFoundException 
				| InstantiationException 
				| IllegalAccessException e) {
			e.printStackTrace();
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
			testRunTime = result.getRunTime()/1000.0;
			executedTestCount = result.getRunCount();
			failedTestCount = result.getFailureCount();
			ignoredTestCount = result.getIgnoreCount();
			this.printTestRunSummary(); // TODO Move to AbstractRuntimeAnalyzer.runTests.
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void printTestRunSummary() {
		System.out.println("Finishing "+testClass
				+" Tests run: "+executedTestCount
				+" Failures: "+failedTestCount
				+" Errors: 0"   //TBD
				+" Skipped: "+ignoredTestCount
				+" Time elapsed: "+ testRunTime +"sec");
//		if(this.failedTestCount !=0) {
//			System.out.println("---------------------Failures--------------------");
//			for(Failure f: result.getFailures()){
//				System.out.println("Header: " + f.getTestHeader());
//				System.out.println("Message: " + f.getMessage());
//				System.out.println("Description: " + f.getDescription());
//				System.out.println("Header: "+f.getTestHeader());
//				System.out.println("Trace: "+f.getTrace());	
//			}
//		}
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

	public static void main(String[] args) {

		addListener();

		LOGGING = readBooleanArgument(LOG);
		String targetDir = readArgumentValue(SUT);

		System.out.println("---------------------------------------------");
		System.out.println("Starting Tacoco JUnitRunner: "+targetDir);
		System.out.println("---------------------------------------------");

		int nThread=Integer.parseInt(readOptionalArgumentValue(THREAD,"1"));
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(targetDir);
		List<String> klasses = probe.getTestClasses();
		List<JUnitRunner> runners = new ArrayList<>();
		JUnitRunner runner=null;
		ExecutorService threadPool = Executors.newFixedThreadPool(nThread);
		for(String testClass : klasses){
			Class<?> c=null;
			try {
				c = Class.forName(testClass);
				if(!shouldRun(c)) {
					continue;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			runner = new JUnitRunner();
			runner.setTest(c);
			threadPool.submit(runner);
			runners.add(runner);
		}
		threadPool.shutdown();
		
		try {
			threadPool.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		double rTime=0;
		int rCnt=0, fCnt=0, iCnt=0;
		for(JUnitRunner r: runners){
			rTime += r.testRunTime;
			rCnt += r.executedTestCount;
			fCnt += r.failedTestCount;
			iCnt += r.ignoredTestCount;
		}

		System.out.println("-------------------------------------------------");
		System.out.println("Tacoco Execution Time: "+ rTime +"sec");
		System.out.println("Run Counts:" + rCnt);
		System.out.println("Failure Counts:" + fCnt);
		System.out.println("Ignore Counts:" + iCnt);
		System.out.println("Number of Thread:" + nThread);
		System.out.println("-------------------------------------------------");
		System.exit(0);
	}
	
	
	
	private static boolean shouldRun(Class<?> c) {
		//Do not run Abstract Class
		if(Modifier.isAbstract(c.getModifiers())) return false;

		for(Method m:c.getMethods()){
			//Run a class which has @Test annotation //JUnit4
			if(m.getAnnotation(Test.class) != null) return true;
			//Run Test Suite Class
			if(m.getName().equals("suite")) return true;
		}
		//JUnit3
		if(TestCase.class.isAssignableFrom(c)) return true;
		return false;
	}
}
