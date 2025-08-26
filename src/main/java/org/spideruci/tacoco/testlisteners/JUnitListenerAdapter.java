package org.spideruci.tacoco.testlisteners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class JUnitListenerAdapter extends RunListener {

	
	private ITacocoTestListener listener;
	private String lastKnownTestClassName = "";
	
	public JUnitListenerAdapter(ITacocoTestListener listener) { 
		this.listener = listener;
	}

	@Override
	public void testRunStarted(Description description) {
		lastKnownTestClassName = testClassName(description);
		listener.onStart(lastKnownTestClassName);
	}

	@Override
	public void testRunFinished(Result result) {
		String testClassName = lastKnownTestClassName == null ? "" : lastKnownTestClassName;
		listener.onEnd(testClassName);
	}

	@Override
	public void testStarted(Description description) {
		this.listener.onTestStart(testName(description));
	}

	@Override
	public void testFinished(Description description) {
		this.listener.onTestEnd(testName(description));
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		this.listener.onTestFailed(testName(failure.getDescription()));
		super.testFailure(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		this.listener.onTestSkipped(testName(description));
		super.testIgnored(description);
	}

	private String testName(Description description) {
		return description.getDisplayName();
	}

	private String testClassName(Description description) {
		return description.getClassName();
	}
}