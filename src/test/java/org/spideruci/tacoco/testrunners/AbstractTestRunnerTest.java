package org.spideruci.tacoco.testrunners;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spideruci.tacoco.probe.AbstractBuildProbe;

public class AbstractTestRunnerTest {

	@BeforeClass
	public static void setUp() throws Exception{
		File junit = new File("resources/spiderMath_JUnit/addition/target/test-classes");
		//File testng = new File("resources/spiderMath_TestNG/target/test-classes");

		new URLClassLoader(((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs()) {
		    @Override
		    public void addURL(URL url) {
		         super.addURL(url);
		    }
		}.addURL(junit.toURI().toURL());
	}
	
	@Test
	public void testGetInstance() {
		//AbstractBuildProbe probe = AbstractBuildProbe.getInstance("resources/spiderMath_JUnit");
		//AbstractTestRunner runner = AbstractTestRunner.getInstance(probe);
		//assertTrue(runner instanceof JUnitRunner);
	}

}
