package org.spideruci.benchmark.spiderMath;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MultiplicationParamTest {

	int expt, a, b;
	
	@Test
	public void testMulti() {
		assertEquals(expt, Multiplication.multi(a, b));
	}
	
	@Parameters
	public static Collection<Integer[]> addedNumbers() {
		return Arrays.asList(new Integer[][] { { 3, 1, 3 }, { 6, 2, 3 },});
	}
	
	public MultiplicationParamTest(int expt, int a, int b){
		this.expt = expt;
		this.a = a;
		this.b = b;
	}

}
