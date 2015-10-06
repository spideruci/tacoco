package org.spideruci.tacoco.testers;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.JUnitRunner;

public class JunitBlinkyListener extends RunListener {
  @Override
  public void testRunStarted(Description description) {

  }

  @Override
  public void testRunFinished(Result result) {
  
  }
  
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
}
