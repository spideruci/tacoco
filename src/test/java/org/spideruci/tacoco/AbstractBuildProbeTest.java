package org.spideruci.tacoco;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.spideruci.tacoco.AbstractBuildProbe.BuilderType;

@RunWith(Parameterized.class)
public class AbstractBuildProbeTest {

	private File testDir;
	private File testConf;
	
	//Parameters
	private String confFileName;
	private BuilderType builderName;
	
	public AbstractBuildProbeTest(String confFileName, BuilderType builderName) {
		this.confFileName = confFileName;
		this.builderName = builderName;
	}
	
	@Parameters
	public static Collection<Object[]> builders() {
	        return Arrays.asList(new Object[][]{
	        	{"pom.xml",BuilderType.MAVEN},
	        	{"build.xml",BuilderType.ANT},
	        	{"build.gradle",BuilderType.GRADLE}
	        	});
	}

	
	@Before
    public void setUp() throws Exception {
		testDir = new File("testDir");
		testDir.mkdir();
		testConf = new File(testDir, confFileName);
		testConf.createNewFile();
		
	}
	
	@Test
	public void getInstanceTest() throws IOException{
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(testDir.getAbsolutePath());
		assertEquals(probe.getBuilderType(),(builderName));
	}

	@After
	public void tearDown() throws Exception {
		testConf.delete();
		testDir.delete();
	}
}
