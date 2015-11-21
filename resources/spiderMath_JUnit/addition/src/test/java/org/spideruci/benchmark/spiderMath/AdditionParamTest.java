package org.spideruci.benchmark.spiderMath;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AdditionParamTest {

	int expt, a, b;
	
	@Test
	public void testAdd() {
		assertEquals(expt, Addition.add(a, b));
	}
	
	@Parameters
	public static Collection<Integer[]> addedNumbers() {
		return Arrays.asList(new Integer[][] { { 3, 1, 2 }, { 5, 2, 3 },});
	}
	
	public AdditionParamTest(int expt, int a, int b){
		this.expt = expt;
		this.a = a;
		this.b = b;
	}

}
