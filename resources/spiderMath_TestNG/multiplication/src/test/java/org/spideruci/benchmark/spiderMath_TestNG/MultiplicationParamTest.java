package org.spideruci.benchmark.spiderMath_TestNG;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MultiplicationParamTest {

	int expt, a, b;
	
	@Test
	public void testMulti() {
		Assert.assertEquals(expt, Multiplication.multi(a, b));
	}
	
	@DataProvider
	public static Object[][] addedNumbers() {
		return new Integer[][] { { 3, 1, 3 }, { 6, 2, 3 },};
	}

}
