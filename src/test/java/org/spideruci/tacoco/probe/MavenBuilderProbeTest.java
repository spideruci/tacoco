package org.spideruci.tacoco.probe;

import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class MavenBuilderProbeTest {
	
	private static AbstractBuildProbe probe;

	@BeforeClass
	public static void setUp() throws IOException{
		FileInputStream testProperties = new FileInputStream("resources/test.properties");
		Properties prop = new Properties();
		prop.load(testProperties);
		MavenBuilderProbeTest.probe = AbstractBuildProbe.getInstance(prop.getProperty("spiderMath"));
	}
	
	/*
	public abstract List<String> getTestClasses();
	public abstract List<String> getClasses();
	public abstract BuilderType getBuilderType();
	public abstract String getClasspath();
	public abstract boolean hasChild();
	public abstract List<Child> getChildren();
	public abstract String getId();
	
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
	*/
	
	@Test
	public void getClasspathTest() throws IOException{
		System.out.print(this.probe.getClasspath());
	}
	

}
