package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.LISTENER;
import static org.spideruci.tacoco.cli.AbstractCli.THREAD;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.testrunners.AbstractTestRunner;

/**
 * This is an AbstractAnalyzer that works with a AbstractTestRunner
 * @author vpalepu
 *
 */
public abstract class AbstractRuntimeAnalyzer extends AbstractAnalyzer {
	
	protected RunListener listener;
	
	@Override
	public void setup() {
		super.setup();
		this.setupRuntimeListener();
	}

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
		
		
		if(this.result == null) {
			this.result = new AnalysisResults();
		}
		
		
		this.result.put("Runtime Execution Time (in sec)", rTime);
		this.result.put("Test Run Counts", rCnt);
		this.result.put("Failure Counts", fCnt);
		this.result.put("Ignore Counts", iCnt);
		this.result.put("Number of Thread", nThread);
	}
	
	
	@Override
	public void printAnalysisSummary() {
		System.out.println("-------------------------------------------------");
		for(Entry<String, Object> entry : this.result) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println("-------------------------------------------------");
	}


}
