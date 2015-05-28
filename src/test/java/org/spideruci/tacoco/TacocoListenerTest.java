package org.spideruci.tacoco;

import java.io.File;

import org.jacoco.agent.rt.internal_773e439.Agent;
import org.jacoco.agent.rt.internal_773e439.core.runtime.AgentOptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runner.Result;

public class TacocoListenerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private AgentOptions options;
	private File execfile;
	private TacocoListener tl;
	
    @Before
    public void setUp() throws Exception {
    		options = new AgentOptions();
    		execfile = new File("jacoco.exec");
    		options.setOutput("file");
    		options.setDestfile(execfile.getAbsolutePath());
    		tl = new TacocoListener();
    		options.setSessionId("testsession");
    		Agent agent = Agent.getInstance(options);
    		agent.startup();
    		tl.testRunStarted(Description.createSuiteDescription("TestClass"));
    }
	@Test
	public void testTestRunStarted() {
		tl.testRunStarted(Description.createSuiteDescription("TestClass"));
	}

	@Test
	public void testTestStarted() {
		tl.testStarted(Description.createSuiteDescription("TestClass"));
	}

	@Test
	public void testTestFinished() {
		try {
			tl.testFinished(Description.createSuiteDescription("TestClass"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testRunFinished() {
		Result rst = new Result();
		tl.testRunFinished(rst);
	}
}
