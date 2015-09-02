package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.CliAble.LOG;
import static org.spideruci.tacoco.cli.CliAble.PM;
import static org.spideruci.tacoco.cli.CliAble.TARGET;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readArgumentValue;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public final class JUnitRunner {
	public static void main(String[] args) {

		boolean log=false;
		if(readOptionalArgumentValue(LOG,"off").equals("on")) log=true;
		String targetDir = readArgumentValue(TARGET);
			
		System.out.println("Starting Tacoco JUnitRunner");
		
		JUnitCore core = new JUnitCore();
		JUnitListener listener = new JUnitListener();
		core.addListener(listener);
		
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(targetDir);
		
		ArrayList<String> klasses = probe.getClasses();
		Class<?>[] cls = new Class<?>[klasses.size()];
		int i=0;
		for(String testClass : klasses){
			try {
				cls[i++] = Class.forName(testClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		//Preparing Parallel JUnit Runner
		String pm = readOptionalArgumentValue(PM,"none");
		boolean classes=false, methods=false;
		if(pm.equals("classes")) classes = true;
		else if(pm.equals("methods")) methods = true;
		else if(pm.equals("both")) {
			classes = true;
			methods = true;
		}
		
		Result result = core.run(new ParallelComputer(classes,methods),cls);
		List<Failure> failures = result.getFailures(); 
		
		/* rerun without parallelism if it fails
		if(!failures.isEmpty()){
			if(classes || methods) result = core.run(cls);
		}
		*/
		
        System.out.println("-------------------------------------------------");
		System.out.println("Tacoco Execution Time: "+result.getRunTime()/1000.0 +"sec");
		System.out.println("Run Counts:" + result.getRunCount());
		System.out.println("Failure Counts:" + result.getFailureCount());
		System.out.println("Ignore Counts:" + result.getIgnoreCount());
		System.out.println("Parallel Mode:" + pm);
		System.out.println("-------------------------------------------------");
		
		if(log) System.out.println(failures);	
	}
}
