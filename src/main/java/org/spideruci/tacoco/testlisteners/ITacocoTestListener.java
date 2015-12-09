package org.spideruci.tacoco.testlisteners;

public interface ITacocoTestListener {
	
	public void onStart();
	public void onTestStart(String testName);
	public void onTestPassed();
	public void onTestFailed();
	public void onTestSkipped();
	public void onTestEnd();
	public void onEnd();
	
}
