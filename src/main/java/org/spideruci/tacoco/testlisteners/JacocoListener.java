package org.spideruci.tacoco.testlisteners;

import java.io.IOException;

import org.jacoco.agent.rt.IAgent;
import org.jacoco.agent.rt.RT;

public class JacocoListener implements ITacocoTestListener{

	private IAgent agent;

	@Override
	public void onStart() {
		agent = RT.getAgent();
	}

	@Override
	public void onTestStart(String testName) {
		agent.setSessionId(testName);
	}

	@Override
	public void onTestPassed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailed() {
		this.agent.setSessionId(agent.getSessionId()+"_F");
	}

	@Override
	public void onTestSkipped() {
		this.agent.setSessionId(agent.getSessionId()+"_I");
	}

	@Override
	public void onTestEnd() {
		try {
			this.agent.dump(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.flush();
	}

	@Override
	public void onEnd() {
		agent.setSessionId("end");
	}

}
