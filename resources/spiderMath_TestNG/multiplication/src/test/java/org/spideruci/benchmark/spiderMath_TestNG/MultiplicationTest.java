package org.spideruci.benchmark.spiderMath_TestNG;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MultiplicationTest {

	@Test
	public void testFail() {
		//assertEquals(1,2);
	}

	@Test
	public void testSuccess1() {
		Assert.assertEquals(4, Multiplication.multi(4, 1));	
	}
	
	@Test
	public void testSuccess2() {
		Assert.assertEquals(12, Multiplication.multi(4, 3));
	}

	@Test(enabled = false)
	public void testIgnore() {
		Assert.assertEquals(5, Multiplication.multi(4, 1));
	}

}
