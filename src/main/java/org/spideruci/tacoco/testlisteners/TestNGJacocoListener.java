package org.spideruci.tacoco.testlisteners;

import java.io.IOException;

import org.jacoco.agent.rt.IAgent;
import org.jacoco.agent.rt.RT;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestNGJacocoListener extends TestListenerAdapter{
	private IAgent agent;

	public TestNGJacocoListener() { 
	}

	@Override
	public void onStart(ITestContext testContext)  {
		System.out.println("Starting TestNGListner");
		agent = RT.getAgent();
	}
	
	
	@Override
	public void onTestStart(ITestResult result) {
		String method = result.getMethod().getMethodName();
		String klass = result.getTestClass().getName();
		String sessionId = method+"("+klass+")";
		System.out.println("Setting sessionId to "+sessionId);
		agent.setSessionId(sessionId);
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		agent.setSessionId(agent.getSessionId()+"_F");
		endTest(result);
	}


	@Override
	public void onTestSkipped(ITestResult result) {
		agent.setSessionId(agent.getSessionId()+"_I");
		endTest(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		endTest(result);
	}

	private void endTest(ITestResult result) {
		System.out.println("Test case finished: " +result.getTestName());
		try {
			agent.dump(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.flush();
	}
	
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		
	}


}
