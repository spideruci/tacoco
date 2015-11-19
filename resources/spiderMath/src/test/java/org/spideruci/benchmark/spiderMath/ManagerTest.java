package org.spideruci.benchmark.spiderMath;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spideruci.benchmark.spiderMath.Manager;

public class ManagerTest {
	
	@Test
	public void getNameTest(){
		Manager m = new Manager();
		assertEquals("SpiderMath", m.getName());
	}

}
