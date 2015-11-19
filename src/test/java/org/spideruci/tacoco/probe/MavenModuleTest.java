package org.spideruci.tacoco.probe;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.spideruci.tacoco.probe.MavenModule;

public class MavenModuleTest {
	
	public static MavenModule module;
	
	@BeforeClass
	public static void setUp() throws IOException{
		MavenModuleTest.module = new MavenModule("resources/spiderMath/addition");
	}
	
	@Test
	public void getTestClassesTest(){
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		
		set1.add("org.spideruci.benchmark.spiderMath.AdditionParamTest");
		set1.add("org.spideruci.benchmark.spiderMath.AdditionTest");
		set2.addAll(this.module.getTestClasses());
		assertEquals(set1, set2);	
	}
	
	@Test
	public void getClassesTest(){
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		
		set1.add("org.spideruci.benchmark.spiderMath.Addition");
		set2.addAll(this.module.getClasses());
		assertEquals(set1, set2);	
	}
	
	@Ignore
	public void getClasspathTest(){
		assertEquals(0, 0);	
	}
	
	
}
