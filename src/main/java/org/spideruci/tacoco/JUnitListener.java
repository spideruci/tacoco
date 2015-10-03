package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.AbstractCli.LOG;
import static org.spideruci.tacoco.cli.AnalyzerCli.readOptionalArgumentValue;

import org.jacoco.agent.rt.IAgent;
import org.jacoco.agent.rt.RT;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class JUnitListener extends RunListener
{

	private IAgent agent;
	private boolean log=false;
	
	public JUnitListener(){
		if(readOptionalArgumentValue(LOG,"off").equals("on")) log=true;	
	}
	
	public void testRunStarted(Description description)
	{
		agent = RT.getAgent();
	}

	public void testRunFinished(Result result)
	{
		agent.setSessionId("end");
	}
	
	public void testStarted(Description description)
	{
		if(log) System.out.println("Setting sessionId to "+description.getDisplayName());
		agent.setSessionId(description.getDisplayName());
	}
	
	public void testFinished(Description description) throws java.lang.Exception
	{	
		if(log) System.out.println("Test case finished: " +description.getDisplayName());
		agent.dump(true);
		System.out.flush();
	}
	
	@Override
	public void testFailure(Failure failure) throws Exception {
		agent.setSessionId(agent.getSessionId()+"_F");
		super.testFailure(failure);
	}
	
	@Override
	public void testIgnored(Description description) throws Exception {
		agent.setSessionId(agent.getSessionId()+"_I");
		super.testIgnored(description);
	}

}