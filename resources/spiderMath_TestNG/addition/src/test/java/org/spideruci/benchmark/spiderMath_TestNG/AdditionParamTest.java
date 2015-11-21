package org.spideruci.benchmark.spiderMath_TestNG;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AdditionParamTest {
	
	@Test(dataProvider = "numberProvider")
	public void testAdd(int expt, int a, int b) {
		assertEquals(expt, Addition.add(a, b));
	}
	
	@DataProvider
	public static Object[][] numberProvider() {
		return new Integer[][] { { 3, 1, 2 }, { 5, 2, 3 },};
	}

}
