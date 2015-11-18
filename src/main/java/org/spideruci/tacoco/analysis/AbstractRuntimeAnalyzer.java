package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.LISTENER;
import static org.spideruci.tacoco.cli.AbstractCli.THREAD;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.testrunners.AbstractTestRunner;

public abstract class AbstractRuntimeAnalyzer extends AbstractAnalyzer {
	
	protected RunListener listener;

	public void setupRuntimeListener() {
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
				this.listener = listener;
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
	
	protected void runTests(List<Class<?>> klasses) {
		List<AbstractTestRunner> runners = new ArrayList<>();
		int nThread  = Integer.parseInt(readOptionalArgumentValue(THREAD, "1"));
		ExecutorService threadPool = Executors.newFixedThreadPool(nThread);
		AbstractTestRunner runner = AbstractTestRunner.getInstance(this.buildProbe);
		if(listener != null) {
			runner.listenThrough(this.listener);
		}
		
		for(Class<?> testClass : klasses) {
			try {
				runner.setTest(testClass);
				if(!runner.shouldRun()) {
					continue;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				continue;
			}
			
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
		for(AbstractTestRunner r: runners) {
			rTime += r.testRunTime;
			rCnt += r.executedTestCount;
			fCnt += r.failedTestCount;
			iCnt += r.ignoredTestCount;
		}

		System.out.println("-------------------------------------------------");
		System.out.println("Runtime Execution Time: "+ rTime +"sec");
		System.out.println("Test Run Counts:" + rCnt);
		System.out.println("Failure Counts:" + fCnt);
		System.out.println("Ignore Counts:" + iCnt);
		System.out.println("Number of Thread:" + nThread);
		System.out.println("-------------------------------------------------");
	}
	


}
