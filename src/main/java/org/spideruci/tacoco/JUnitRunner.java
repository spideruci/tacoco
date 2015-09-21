package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.CliAble.LOG;
import static org.spideruci.tacoco.cli.CliAble.SUT;
import static org.spideruci.tacoco.cli.CliAble.THREAD;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readArgumentValue;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;

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

import junit.framework.TestCase;

public final class JUnitRunner extends Thread{
	
	private Class<?> testClass;
	private static JUnitCore core = new JUnitCore();
	static{
		core.addListener(new JUnitListener());
	}
	
	double runTime=0;
	int runCnt=0, failCnt=0, ignoreCnt=0;
	
    public JUnitRunner(Class<?> testClass) {
    	this.testClass = testClass;
	}

	public void run() {
    	try {
			System.out.println("Starting "+testClass);
			Result result = core.run(testClass);
			runTime=result.getRunTime()/1000.0;
			runCnt=result.getRunCount();
			failCnt=result.getFailureCount();
			ignoreCnt=result.getIgnoreCount();
			
			System.out.println("Finishing "+testClass
								+" Tests run: "+runCnt
								+" Failures: "+failCnt
								+" Errors: 0"   //TBD
								+" Skipped: "+ignoreCnt
								+" Time elapsed: "+ runTime +"sec");
			if(result.getFailureCount() !=0) {
				System.out.println("---------------------Failures--------------------");
				for(Failure f: result.getFailures()){
					System.out.println("Header: " + f.getTestHeader());
					System.out.println("Message: " + f.getMessage());
					System.out.println("Description: " + f.getDescription());
					System.out.println("Header: "+f.getTestHeader());
					System.out.println("Trace: "+f.getTrace());	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public static void main(String[] args) {

		boolean log=false;
		if(readOptionalArgumentValue(LOG,"off").equals("on")) log=true;
		String targetDir = readArgumentValue(SUT);

		System.out.println("---------------------------------------------");
		System.out.println("Starting Tacoco JUnitRunner: "+targetDir);
		System.out.println("---------------------------------------------");
		
		int nThread=Integer.parseInt(readOptionalArgumentValue(THREAD,"1"));
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(targetDir);
		List<String> klasses = probe.getClasses();
		
		List<JUnitRunner> runners = new ArrayList<>();
		JUnitRunner runner=null;
		ExecutorService threadPool = Executors.newFixedThreadPool(nThread);
		for(String testClass : klasses){
			Class<?> c=null;
			try {
				//if(!testClass.matches("com.google.common.cache.Lo.*")) continue;
				c = Class.forName(testClass);
				if(!shouldRun(c)) continue;
			} catch (Throwable e) {
				e.printStackTrace();
			}
			runner = new JUnitRunner(c);
			threadPool.submit(runner);
			runners.add(runner);
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);//Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		double rTime=0;
		int rCnt=0, fCnt=0, iCnt=0;
		for(JUnitRunner r: runners){
			rTime += r.runTime;
			rCnt += r.runCnt;
			fCnt += r.failCnt;
			iCnt += r.ignoreCnt;
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
		//Run a class which has @Test annotation //JUnit4
		for(Method m:c.getMethods()){
			if(m.getAnnotation(Test.class) != null) return true;  
		}
		//Run a class which extends TestCase //JUnit3
		if(c.getSuperclass().equals(TestCase.class)) return true;  
		return false;
	}
}
