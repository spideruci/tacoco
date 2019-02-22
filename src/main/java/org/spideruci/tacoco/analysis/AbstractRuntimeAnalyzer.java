package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.LISTENERS;
import static org.spideruci.tacoco.cli.AbstractCli.THREAD;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.spideruci.tacoco.testlisteners.ITacocoTestListener;
import org.spideruci.tacoco.testrunners.AbstractTestRunner;

/**
 * This is an AbstractAnalyzer that works with a AbstractTestRunner
 * @author vpalepu
 *
 */
public abstract class AbstractRuntimeAnalyzer extends AbstractAnalyzer {

	protected List<ITacocoTestListener> listeners;

	@Override
	public void setup() {
		super.setup();
		this.setupRuntimeListener();
	}

	public void setupRuntimeListener() {
		this.listeners = new ArrayList<>();
		
		String listenerClassNames = readOptionalArgumentValue(LISTENERS, null);
		if(listenerClassNames == null) {
			return;
		}
		for(String listenerClassName : listenerClassNames.split(":")){
			try {
				Class<?> listenerClass = Class.forName(listenerClassName);
				if(listenerClass == null) {
					continue;
				}
				Object listener = listenerClass.newInstance();
				if(listener instanceof ITacocoTestListener) this.listeners.add((ITacocoTestListener)listener);
			} catch (ClassNotFoundException 
					| InstantiationException 
					| IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	protected void runTests(List<Class<?>> klasses) {
		List<Future<AnalysisResults>> futureAnalysisResults = new ArrayList<>();
		int nThread  = Integer.parseInt(readOptionalArgumentValue(THREAD, "1"));
		ExecutorService threadPool = Executors.newFixedThreadPool(nThread);

		AbstractTestRunner runner = AbstractTestRunner.getInstance(this.buildProbe);
		
		if(runner == null)
		{
			System.out.println("No test class found");
		}
		else
		{
			for(ITacocoTestListener listener : this.listeners) {
				runner.listenThrough(listener);
			}
	
			for(Class<?> testClass : klasses) {
				try {
					if(!runner.shouldRun(testClass)) {
						continue;
					}
				} catch (Throwable e) {
					e.printStackTrace();
					continue;
				}
	
				Future<AnalysisResults> futureResults = 
						threadPool.submit(runner.getExecutableTest(testClass));
				futureAnalysisResults.add(futureResults);
			}
			threadPool.shutdown();
	
			try {
				threadPool.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			double rTime=0;
			int rCnt=0, fCnt=0, iCnt=0;
			for(Future<AnalysisResults> futureResults : futureAnalysisResults) {
				try {
					AnalysisResults results = futureResults.get();
					runner.printTestRunSummary(results);
					rTime += runner.testRunTime;
					rCnt += runner.executedTestCount;
					fCnt += runner.failedTestCount;
					iCnt += runner.ignoredTestCount;
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
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
	}


	@Override
	public void printAnalysisSummary() {
		if(this.result==null) return;
		System.out.println("-------------------------------------------------");
		for(Entry<String, Object> entry : this.result) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println("-------------------------------------------------");
	}


}
