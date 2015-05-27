package org.spideruci.tacoco;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.jacoco.agent.rt.RT;
import org.jacoco.agent.rt.IAgent;


public class TacocoListener extends RunListener
{
	private IAgent agent;

	public void testRunStarted(Description description)
	{
		System.out.println("Number of test cases to execute: " + description.testCount());
		agent = RT.getAgent();
	}

	public void testRunFinished(Result result)
	{
		if(result != null) {
		  System.out.println("Number of test cases executed: " + result.getRunCount());
		}
		agent.setSessionId("end");
	}

	public void testStarted(Description description)
	{
		System.out.println("Test case started.");
		System.out.println("Setting sessionId to "+description.getDisplayName());
		agent.setSessionId(description.getDisplayName());
	}
	
	public void testFinished(Description description) throws java.lang.Exception
	{
		int sleepLength = 10;
		System.out.println("Test case finished.");
		agent.dump(true);
		System.out.println("Going to sleep for "+sleepLength+" ms.");
		System.out.flush();
		Thread.sleep(sleepLength);
		System.out.println("Done sleeping.");
		System.out.flush();
		Thread.sleep(10);
	}
}