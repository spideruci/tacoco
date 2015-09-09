package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.CliAble.LOG;
import static org.spideruci.tacoco.cli.CliAble.PM;
import static org.spideruci.tacoco.cli.CliAble.TARGET;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readArgumentValue;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public final class JUnitRunner extends Thread{
	
	private String testClass="";
	private static JUnitCore core = new JUnitCore();
	static{
		//core.addListener(new JUnitListener());
	}
	
	double runTime=0;
	int runCnt=0, failCnt=0, ignoreCnt=0;
	
    public JUnitRunner(String testClass) {
    	this.testClass = testClass;
	}

	public void run() {
    	try {
			System.out.println("Starting "+testClass);
			Result result = core.run(Class.forName(testClass));
			
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
			if(false && result.getFailureCount() !=0) {
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
		String targetDir = readArgumentValue(TARGET);

		System.out.println("---------------------------------------------");
		System.out.println("Starting Tacoco JUnitRunner: "+targetDir);
		System.out.println("---------------------------------------------");
		
		
		//Preparing Parallel JUnit Runner
		String pm = readOptionalArgumentValue(PM,"1");
		

		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(targetDir);
		List<String> klasses = probe.getClasses();
		
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		List<JUnitRunner> runners = new ArrayList<>();
		for(String testClass : klasses){
			JUnitRunner r = new JUnitRunner(testClass);
			threadPool.submit(r);
			runners.add(r);
		}
		threadPool.shutdown();
		
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
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
		System.out.println("Parallel Mode:" + pm);
		System.out.println("-------------------------------------------------");
		System.exit(0);
	}
}
