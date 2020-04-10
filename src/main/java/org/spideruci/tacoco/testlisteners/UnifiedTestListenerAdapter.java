package org.spideruci.tacoco.testlisteners;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

public class UnifiedTestListenerAdapter implements TestExecutionListener {

    private final ITacocoTestListener listener;
    public UnifiedTestListenerAdapter(final ITacocoTestListener listener) {
        this.listener = listener;
    }

    private String getUniqueTestName(TestIdentifier testIdentifier) {
        final String testUid = testIdentifier.getUniqueId();
        final String testName = testIdentifier.getDisplayName();
        final String testUniqueName = String.format("%s.%s", testName, testUid);
        return testUniqueName;
    }

    @Override
    public void executionStarted(final TestIdentifier testIdentifier) {
        if (!testIdentifier.isContainer()) {
            final String testUniqueName = getUniqueTestName(testIdentifier);
            listener.onTestStart(testUniqueName);
        }
    }

    @Override
    public void executionSkipped(final TestIdentifier testIdentifier, final String reason) {
        if (!testIdentifier.isContainer()) {
            listener.onTestSkipped();
        }
    }

    @Override
    public void executionFinished(final TestIdentifier testIdentifier, final TestExecutionResult testExecutionResult) {
        if (!testIdentifier.isContainer()) {
            final Status status = testExecutionResult.getStatus();

            switch (status) {
                case SUCCESSFUL:
                    listener.onTestPassed();
                    break;
                case FAILED:
                case ABORTED:
                    listener.onTestFailed();
                    break;
            }

            listener.onTestEnd();
        }
    }

    public void testPlanExecutionStarted(final TestPlan testPlan) {
        listener.onStart();
    }

    public void testPlanExecutionFinished(final TestPlan testPlan) {
        listener.onEnd();
	}
}