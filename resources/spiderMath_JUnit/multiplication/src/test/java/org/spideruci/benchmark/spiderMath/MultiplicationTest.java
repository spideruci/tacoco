package org.spideruci.benchmark.spiderMath;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class MultiplicationTest {

	@Test
	public void testFail() {
		//assertEquals(1,2);
	}

	@Test
	public void testSuccess1() {
		assertEquals(4, Multiplication.multi(4, 1));	
	}
	
	@Test
	public void testSuccess2() {
		assertEquals(12, Multiplication.multi(4, 3));
	}

	@Ignore
	public void testIgnore() {
		assertEquals(5, Multiplication.multi(4, 1));
	}

}
