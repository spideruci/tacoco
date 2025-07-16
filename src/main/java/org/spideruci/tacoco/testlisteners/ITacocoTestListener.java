package org.spideruci.tacoco.testlisteners;

public interface ITacocoTestListener {
	
	public void onStart();
	public void onTestStart(String testName);
	public void onTestPassed(String testName);
	public void onTestFailed(String testName);
	public void onTestSkipped(String testName);
	public void onTestEnd(String testName);
	public void onEnd();
	
}
