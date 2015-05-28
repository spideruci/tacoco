package org.spideruci.tacoco.reporting.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.internal.analysis.LineImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestCountersToStatus {
  
  @Parameters
  public static Collection<Object[]> data() {
    ArrayList<Object[]> tests = new ArrayList<>();
    
    tests.add(new Object[] { 0,0,0,0, ICounter.EMPTY });
//    tests.add(new Object[] { 0,0,0,1, ICounter.EMPTY });
//    tests.add(new Object[] { 0,0,1,0, ICounter.EMPTY });
//    tests.add(new Object[] { 0,0,1,1, ICounter.EMPTY });
    tests.add(new Object[] { 0,1,0,0, ICounter.NOT_COVERED });
    tests.add(new Object[] { 0,1,0,1, ICounter.NOT_COVERED });
//    tests.add(new Object[] { 0,1,1,0, ICounter.EMPTY });
//    tests.add(new Object[] { 0,1,1,1, ICounter.PARTLY_COVERED });
    tests.add(new Object[] { 1,0,0,0, ICounter.FULLY_COVERED });
    tests.add(new Object[] { 1,0,0,1, ICounter.PARTLY_COVERED });
    tests.add(new Object[] { 1,0,1,0, ICounter.FULLY_COVERED });
    tests.add(new Object[] { 1,0,1,1, ICounter.PARTLY_COVERED });
    tests.add(new Object[] { 1,1,0,0, ICounter.PARTLY_COVERED });
    tests.add(new Object[] { 1,1,0,1, ICounter.PARTLY_COVERED });
    tests.add(new Object[] { 1,1,1,0, ICounter.PARTLY_COVERED });
    tests.add(new Object[] { 1,1,1,1, ICounter.PARTLY_COVERED });

    return tests;
  }
  
  private int ic;
  private int im;
  private int bc;
  private int bm;
  private int status;

  public TestCountersToStatus(int ic, int im, int bc, int bm, int status) {
    this.ic = ic;
    this.im = im;
    this.bc = bc;
    this.bm = bm;
    this.status = status;
  }

  @Test
  public void statusConverterShouldMatchExpectedStatus() {
    //given
    LineCoverageCoder coder = new LineCoverageCoder();
    int[] counters = new int[] { ic, im, bc, bm};
    
    //when
    int actualStatus = coder.toStatus(counters);
    
    //then
    assertEquals(this.status, actualStatus);
  }

}
