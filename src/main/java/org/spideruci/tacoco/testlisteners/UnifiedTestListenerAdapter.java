package org.spideruci.tacoco.testlisteners;

import java.util.Iterator;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.spideruci.tacoco.testrunners.AbstractTestRunner.TestType;
import org.junit.platform.engine.TestDescriptor.Type;

public class UnifiedTestListenerAdapter implements TestExecutionListener {

    private final ITacocoTestListener listener;
    public UnifiedTestListenerAdapter(final ITacocoTestListener listener) {
        this.listener = listener;
    }

    private String getUniqueTestName(TestIdentifier testIdentifier) {
        // final String testUid = testIdentifier.getUniqueId();
        // final String testName = testIdentifier.getDisplayName();
        // final String testUniqueName = String.format("%s.%s", testName, testUid);
        return testIdentifier.getLegacyReportingName();
    }

    @Override
    public void executionStarted(final TestIdentifier testIdentifier) {
        if (testIdentifier.isContainer()) {
            return;
        }

        final String testUniqueName = getUniqueTestName(testIdentifier);
        listener.onTestStart(testUniqueName);
    }

    @Override
    public void executionSkipped(final TestIdentifier testIdentifier, final String reason) {
        if (testIdentifier.isContainer()) {
            return;
        }

        final String testUniqueName = getUniqueTestName(testIdentifier);
        listener.onTestSkipped(testUniqueName);
    }

    @Override
    public void executionFinished(final TestIdentifier testIdentifier, final TestExecutionResult testExecutionResult) {
        if (testIdentifier.isContainer()) {
            return;
        }
        final Status status = testExecutionResult.getStatus();
        final String testUniqueName = getUniqueTestName(testIdentifier);

        switch (status) {
            case SUCCESSFUL:
                listener.onTestPassed(testUniqueName);
                break;
            case FAILED:
            case ABORTED:
                listener.onTestFailed(testUniqueName);
                break;
        }

        listener.onTestEnd(testUniqueName);
    }

    public void testPlanExecutionStarted(final TestPlan testPlan) {
        listener.onStart(testClassName(testPlan));
    }

    public void testPlanExecutionFinished(final TestPlan testPlan) {
        listener.onEnd(testClassName(testPlan));
	}

    private String testClassName(final TestPlan testPlan) {
        Iterator<TestIdentifier> testItr = testPlan.getRoots().iterator();
        while (testItr.hasNext()) {
            TestIdentifier testId = testItr.next();
            for (TestIdentifier childTestIdentifier : testPlan.getChildren(testId)) {
                if (childTestIdentifier.getType() == Type.CONTAINER && childTestIdentifier.getUniqueId().contains("[runner:")) {
                    return childTestIdentifier.getLegacyReportingName();
                }
            }
        }

        return "";
    }
}