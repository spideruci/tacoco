package org.spideruci.tacoco.testlisteners;

import java.util.Collections;

import org.junit.Test;
import org.testng.ITestListener;
import org.testng.TestNG;

public class TestNGJacocoListenerTest {

	@Test
	public void test() {
		TestNG TESTNG = new TestNG();
		final ITestListener listener = new TestNGJacocoListener();
		TESTNG.addListener(listener);
		/*
		try {
			TESTNG.run();
		} finally {
			// yes this is hideous
			TESTNG.getTestListeners().remove(listener);
		}*/
	}

}
