package org.spideruci.benchmark.spiderMath;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class AdditionTest {

	@Test
	public void testFail() {
		//assertEquals(1,2);
	}

	@Test
	public void testSuccess1() {
		assertEquals(5, Addition.add(4, 1));	
	}
	
	@Test
	public void testSuccess2() {
		assertEquals(7, Addition.add(4, 3));
	}

	@Ignore
	public void testIgnore() {
		assertEquals(5, Addition.add(4, 1));
	}

}
