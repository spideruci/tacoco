package org.spideruci.tacoco;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MavenBuilderProbeTest {
	
	private File pom_hasChild;
	private File pom_hasNoChild;
	
	@Before
    public void setUp() throws Exception {
		//testDir = new File("testDir");
		//testDir.mkdir();
		//testConf = new File(testDir, confFileName);
		//testConf.createNewFile();
		
	}
	
	@Test
	public void hasTest() throws IOException{
		//AbstractBuildProbe probe = AbstractBuildProbe.getInstance(testDir.getAbsolutePath());
		//assertEquals(probe.getBuilderType(),(builderName));
	}

	@After
	public void tearDown() throws Exception {
		//testConf.delete();
		//testDir.delete();
	}
}
