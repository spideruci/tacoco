package org.spideruci.tacoco.testlisteners;

public class BlinkyListener implements ITacocoTestListener {
	/*
	@Override
	public void testStarted(Description description) {
		if(JUnitRunner.LOGGING) {
			System.out.println("Setting sessionId to "+ description.getDisplayName());
		}
	}

	@Override
	public void testFinished(Description description) throws java.lang.Exception {
		if(JUnitRunner.LOGGING) {
			System.out.println("Test case finished: " + description.getDisplayName());
		}
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		super.testIgnored(description);
	}
	*/
	@Override
	public void onStart() {
		// do nothing
		
	}

	@Override
	public void onTestStart(String testName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestPassed(String testName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailed(String testName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSkipped(String testName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestEnd(String testName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}
}
