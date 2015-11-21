package org.spideruci.tacoco.testlisteners;

import static org.spideruci.tacoco.cli.AbstractCli.DB;

import java.io.File;

import org.jacoco.agent.rt.IAgent;
import org.jacoco.agent.rt.RT;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.spideruci.tacoco.db.CreateSQLiteDB;
import org.spideruci.tacoco.testrunners.JUnitRunner;


public class JUnitJacocoListener extends RunListener {

	private IAgent agent;

	public JUnitJacocoListener() { }

	@Override
	public void testRunStarted(Description description) {
		agent = RT.getAgent();
	}

	@Override
	public void testRunFinished(Result result) {
		agent.setSessionId("end");
	}

	@Override
	public void testStarted(Description description) {
		if(JUnitRunner.LOGGING) {
			System.out.println("Setting sessionId to "+description.getDisplayName());
		}
		agent.setSessionId(description.getDisplayName());
	}

	@Override
	public void testFinished(Description description) throws java.lang.Exception {
		if(JUnitRunner.LOGGING) {
			System.out.println("Test case finished: " +description.getDisplayName());
		}
		agent.dump(true);
		System.out.flush();
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		agent.setSessionId(agent.getSessionId()+"_F");
		super.testFailure(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		agent.setSessionId(agent.getSessionId()+"_I");
		super.testIgnored(description);
	}

	@SuppressWarnings("unused")
	private static void storeInDb(String outdir, String projectName, String targetDir, File exec) {
		String dbFile = outdir+"/"+projectName+".db";
		if(System.getProperties().containsKey(DB)) {
			try {
				CreateSQLiteDB.dump(dbFile, targetDir, exec.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}