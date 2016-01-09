package org.spideruci.tacoco.probe;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.spideruci.tacoco.util.PathBuilder;

public class MavenBuilderProbeTest {

	private static AbstractBuildProbe probe;

	@BeforeClass
	public static void setUp() throws IOException{
        final String targetPath = new PathBuilder().path("resources").path("spiderMath_JUnit").buildFilePath();
        MavenBuilderProbeTest.probe = AbstractBuildProbe.getInstance(targetPath);
	}

	@Test
	public void getTestClassesTest() throws IOException{
		Set<String> s1 = new HashSet<>();
		Set<String> s2 = new HashSet<>();

		s1.add("org.spideruci.benchmark.spiderMath.AdditionParamTest");
		s1.add("org.spideruci.benchmark.spiderMath.AdditionTest");
		s1.add("org.spideruci.benchmark.spiderMath.MultiplicationParamTest");
		s1.add("org.spideruci.benchmark.spiderMath.MultiplicationTest");
		s2.addAll(probe.getTestClasses());
		assertEquals(s1,s2);
	}

	@Test
	public void getClassesTest() throws IOException{
		Set<String> s1 = new HashSet<>();
		Set<String> s2 = new HashSet<>();

		s1.add("org.spideruci.benchmark.spiderMath.Addition");
		s1.add("org.spideruci.benchmark.spiderMath.Multiplication");
		s2.addAll(probe.getClasses());
		assertEquals(s1,s2);
	}


	@Test
	public void getClasspathTest() throws IOException{
		System.out.print(probe.getClasspath());
	}


}
