package org.spideruci.tacoco.testlisteners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class JUnitListenerAdapter extends RunListener {

	
	private ITacocoTestListener listener;
	
	public JUnitListenerAdapter(ITacocoTestListener listener) { 
		this.listener = listener;
	}

	@Override
	public void testRunStarted(Description description) {
		listener.onStart();
	}

	@Override
	public void testRunFinished(Result result) {
		listener.onEnd();
	}

	@Override
	public void testStarted(Description description) {
		this.listener.onTestStart(description.getDisplayName());
	}

	@Override
	public void testFinished(Description description) {
		this.listener.onTestEnd();
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		this.listener.onTestFailed();
		super.testFailure(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		this.listener.onTestSkipped();
		super.testIgnored(description);
	}

}