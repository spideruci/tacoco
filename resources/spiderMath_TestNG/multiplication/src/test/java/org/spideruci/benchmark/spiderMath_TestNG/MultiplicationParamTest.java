package org.spideruci.benchmark.spiderMath_TestNG;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MultiplicationParamTest {

	int expt, a, b;
	
	@Test(dataProvider = "numberProvider")
	public void testMulti() {
		Assert.assertEquals(expt, Multiplication.multi(a, b));
	}
	
	@DataProvider
	public static Object[][] numberProvider() {
		return new Integer[][] { { 3, 1, 3 }, { 6, 2, 3 },};
	}

}
