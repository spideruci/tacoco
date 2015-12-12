package org.spideruci.tacoco.testlisteners;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestNGListenerAdapter extends TestListenerAdapter{

	private ITacocoTestListener listener;
	
	public TestNGListenerAdapter(ITacocoTestListener listener) { 
		this.listener = listener;
	}

	@Override
	public void onStart(ITestContext testContext) {
		this.listener.onStart();
	}

	@Override
	public void onTestStart(ITestResult result) {

		String method = result.getMethod().getMethodName();
		String klass = result.getTestClass().getName();
		StringBuilder sb = new StringBuilder();
		for(Object each : result.getParameters()){
			sb.append("|"+each.hashCode());
		}
		if(sb.length()>0){
			sb.setCharAt(0, '[');
			sb.append(']');
		}

		String sessionId = method+sb.toString()+"("+klass+")";
		this.listener.onTestStart(sessionId);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		this.listener.onTestFailed();
		endTest(result);
	}


	@Override
	public void onTestSkipped(ITestResult result) {
		this.listener.onTestSkipped();
		endTest(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		endTest(result);
	}

	private void endTest(ITestResult result) {
		this.listener.onTestEnd();
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}


}
