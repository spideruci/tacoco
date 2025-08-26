package org.spideruci.tacoco.testlisteners;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestNGListenerAdapter extends TestListenerAdapter {

	private ITacocoTestListener listener;
	
	public TestNGListenerAdapter(ITacocoTestListener listener) { 
		this.listener = listener;
	}

	@Override
	public void onStart(ITestContext testContext) {
		super.onStart(testContext);
		this.listener.onStart(testContext.getName());
	}

	@Override
	public void onFinish(ITestContext testContext) {
		super.onFinish(testContext);
		this.listener.onEnd(testContext.getName());
	}

	@Override
	public void onTestStart(ITestResult result) {
		super.onTestStart(result);
		this.listener.onTestStart(testName(result));
	}

	@Override
	public void onTestFailure(ITestResult result) {
		super.onTestFailure(result);
		this.listener.onTestFailed(testName(result));
		endTest(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		super.onTestSkipped(result);
		this.listener.onTestSkipped(testName(result));
		endTest(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		super.onTestSuccess(result);
		endTest(result);
	}

	private void endTest(ITestResult result) {
		this.listener.onTestEnd(testName(result));
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}

	private String testName(ITestResult result) {
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
		return sessionId;
	}


}
