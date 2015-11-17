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

public class MavenModuleTest {
	
	public static MavenModule module;
	
	@BeforeClass
	public static void setUp() throws IOException{
		FileInputStream testProperties = new FileInputStream("resources/test.properties");
		Properties prop = new Properties();
		prop.load(testProperties);
		MavenModuleTest.module = new MavenModule(prop.getProperty("spiderMath"));
	}
	
	@Test
	public void getTestClassesTest(){
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		
		set1.add("org.spideruci.benchmark.spiderMath.ManagerTest");
		set2.addAll(this.module.getTestClasses());
		assertEquals(set1, set2);	
	}
	
	@Test
	public void getClassesTest(){
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		
		set1.add("org.spideruci.benchmark.spiderMath.Manager");
		set2.addAll(this.module.getClasses());
		assertEquals(set1, set2);	
	}
	
	@Ignore
	public void getClasspathTest(){
		assertEquals(0, 0);	
	}
	
	
}
