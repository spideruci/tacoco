package org.spideruci.tacoco.testrunners;

import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.concurrent.Callable;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;
import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;
import org.spideruci.tacoco.testlisteners.UnifiedTestListenerAdapter;

public class UnifiedTestRunner extends AbstractTestRunner {

    private final static String TEST_CLASS_NAME = "test-class-name";
    private final static String TEST_SUMMARY = "test-summary";

    final Launcher launcher = LauncherFactory.create();

    public static boolean containsExecutableTest(final Class<?> test) {
        try {
            final LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(test)).build();
            final Launcher launcher = LauncherFactory.create();
            final TestPlan testplan = launcher.discover(discoveryRequest);
            final boolean containsTests = testplan.containsTests();
            return containsTests;
        } catch (final Exception e) {
            return false;
        }
    }

    private LauncherDiscoveryRequest discoveryRequest(final Class<?> test) {
        try {
            final LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(test)).build();
            return discoveryRequest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean shouldRun(final Class<?> test) {
        return UnifiedTestRunner.containsExecutableTest(test);
    }

    @Override
    public void listenThrough(final ITacocoTestListener listener) {
        final UnifiedTestListenerAdapter testListenerAdapter = new UnifiedTestListenerAdapter(listener);
        launcher.registerTestExecutionListeners(testListenerAdapter);
    }

    @Override
    public Callable<AnalysisResults> getExecutableTest(final Class<?> test) {
        final LauncherDiscoveryRequest discoveryRequest = this.discoveryRequest(test);
        final Launcher launcher = this.launcher;
        final String testClassName = test.getName();

        final Callable<AnalysisResults> execTest = new Callable<AnalysisResults>() {

            @Override
            public AnalysisResults call() throws Exception {
                try {
                    if (discoveryRequest == null) {
                        return null;
                    }

                    final SummaryGeneratingListener sGeneratingListener = new SummaryGeneratingListener();

                    launcher.execute(discoveryRequest, sGeneratingListener);
                    final TestExecutionSummary summary = sGeneratingListener.getSummary();
    
                    final AnalysisResults results = new AnalysisResults();
                    results.put(TEST_CLASS_NAME, testClassName);
                    results.put(TEST_SUMMARY, summary);
                    return results;
                } catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        return execTest;
    }

    @Override
    public void printTestRunSummary(final AnalysisResults results) {
        if (results == null || results.iterator() == null || !results.iterator().hasNext()) {
            return;
        }

        final TestExecutionSummary summary = results.get(TEST_SUMMARY);
        if (summary == null) {
            return;
        }

        final String testName = results.get(TEST_CLASS_NAME);
        if (testName == null) {
            return;
        }

        try {
            this.testRunTime = (summary.getTimeFinished() - summary.getTimeStarted()) / 1000.0;
            this.executedTestCount = (int) summary.getTestsStartedCount();
            this.failedTestCount = (int) (summary.getTestsFailedCount() + summary.getTestsAbortedCount());
            this.ignoredTestCount = (int) summary.getTestsSkippedCount();
    
            System.out.println("Finishing " + testName + " Tests run: " + executedTestCount + " Failures: "
                    + failedTestCount + " Errors: " + summary.getTestsAbortedCount() + " Skipped: " + ignoredTestCount
                    + " Time elapsed: " + testRunTime + "sec");
    
            if (this.failedTestCount != 0) {
                System.out.println("---------------------Failures--------------------");
                for (final Failure f : summary.getFailures()) {
                    System.out.println("Test Name: " + f.getTestIdentifier().getDisplayName());
                    System.out.println("Test Identifier: " + f.getTestIdentifier().getUniqueId());
    
                    System.out.println("Message: " + f.getException().getMessage());
                    System.out.println("Description: " + f.getException().getCause());
                    System.out.println("Trace: ");
                    f.getException().printStackTrace();	
                }
            }
        } catch (Exception e) {
            System.err.println("---------------------Tacoco Error--------------------");
            System.err.printf("Failed to parse Analysis Results for testName: %s\n", testName);
            e.printStackTrace();
        }

    }

}