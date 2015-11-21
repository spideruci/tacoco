package org.spideruci.tacoco.testlisteners;

import java.io.IOException;

import org.jacoco.agent.rt.IAgent;
import org.jacoco.agent.rt.RT;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGJacocoListener implements ITestListener {
	private IAgent agent;

	public TestNGJacocoListener() { }

	@Override
	public void onStart(ITestContext ctx) {
		agent = RT.getAgent();
	}
	
	@Override
	public void onFinish(ITestContext ctx) {
		agent.setSessionId("end");
	}
	
	@Override
	public void onTestStart(ITestResult result) {
		System.out.println("Setting sessionId to "+result.getTestName());
		agent.setSessionId(result.getTestName());
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
