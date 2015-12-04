package org.spideruci.benchmark.spiderMath_TestNG;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MultiplicationParamTest {


	@Test(dataProvider = "numberProvider")
	public void testMulti(int expt, int a, int b) {
		Assert.assertEquals(expt, Multiplication.multi(a, b));
	}
	
	@DataProvider
	public static Object[][] numberProvider() {
		return new Integer[][] { { 3, 1, 3 }, { 6, 2, 3 },};
	}

}
