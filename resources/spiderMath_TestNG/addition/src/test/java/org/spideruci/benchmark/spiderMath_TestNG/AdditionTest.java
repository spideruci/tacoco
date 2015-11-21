package org.spideruci.benchmark.spiderMath_TestNG;

import org.spideruci.benchmark.spiderMath_TestNG.Addition;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdditionTest {

	@Test
	public void testFail() {
		//assertEquals(1,2);
	}

	@Test
	public void testSuccess1() {
		Assert.assertEquals(5, Addition.add(4, 1));	
	}
	
	@Test
	public void testSuccess2() {
		Assert.assertEquals(7, Addition.add(4, 3));
	}

	@Test(enabled = false)
	public void testIgnore() {
		Assert.assertEquals(5, Addition.add(4, 1));
	}

}
