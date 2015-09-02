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

@RunWith(Parameterized.class)
public class AbstractBuildProbeTest {

	private File testDir;
	private File testConf;
	
	//Parameters
	private String confFileName;
	private String builderName;
	
	public AbstractBuildProbeTest(String confFileName, String builderName) {
		this.confFileName = confFileName;
		this.builderName = builderName;
	}
	
	@Parameters
	public static Collection<String[]> builders() {
	        return Arrays.asList(new String[][]{
	        	{"pom.xml","MAVEN"},
	        	{"build.xml","ANT"},
	        	{"build.gradle","GRADLE"}
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
		assertEquals(probe.getBuilderName(),(builderName));
	}

	@After
	public void tearDown() throws Exception {
		testConf.delete();
		testDir.delete();
	}
}
