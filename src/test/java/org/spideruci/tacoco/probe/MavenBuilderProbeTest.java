package org.spideruci.tacoco.probe;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

public class MavenBuilderProbeTest {
	
	private static AbstractBuildProbe probe;

	@BeforeClass
	public static void setUp() throws IOException{
		MavenBuilderProbeTest.probe = AbstractBuildProbe.getInstance("resources/spiderMath");
	}
	
	@Test
	public void getTestClassesTest() throws IOException{
		for(String s:probe.getTestClasses()){
			System.out.print(s + ", ");
		}
	}

	@Test
	public void getClassesTest() throws IOException{
		for(String s:probe.getClasses()){
			System.out.print(s + ", ");
		}
	}
	
	
	@Test
	public void getClasspathTest() throws IOException{
		System.out.print(probe.getClasspath());
	}
	

}
