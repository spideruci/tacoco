package org.spideruci.tacoco.reporting.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jacoco.core.analysis.ILine;
import org.jacoco.core.internal.analysis.CounterImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class TestLineCoverageCoder {
  
  @Parameters
  public static Collection<Object[]> data() {
    ArrayList<Object[]> counters = new ArrayList<>();
    int[] array = new int[] {0, 1, 2, 3, 252, 253, 254, 255};
    for(int i : array ) {
      for(int j : array) {
        for (int k : array) {
          for (int l : array) {
            counters.add(new Object[] {i, j, k, l});
          }
        }
      }
    }
    
    return counters;
  }
  
  private int ic;
  private int im;
  private int bc;
  private int bm;

  public TestLineCoverageCoder(int ic, int im, int bc, int bm) {
    this.ic = ic;
    this.im = im;
    this.bc = bc;
    this.bm = bm;
  }

  @Test
  public void compactEncodedCountersShouldEqualOriginalCounters() {
    // given
    ILine lineCoverage = mock(ILine.class);
    when(lineCoverage.getInstructionCounter())
          .thenReturn(CounterImpl.getInstance(im, ic));
    when(lineCoverage.getBranchCounter())
          .thenReturn(CounterImpl.getInstance(bm, bc));
    LineCoverageCoder coder = new LineCoverageCoder();
    
    //when
    int codedCoverage = coder.encode(lineCoverage);
    //and
    int[] decodedCoverage = coder.decode(codedCoverage);
    String decodedCovArrayString = Arrays.toString(decodedCoverage);
    
    //then
    assertEquals(decodedCovArrayString, ic, decodedCoverage[0]);
    //and
    assertEquals(decodedCovArrayString, im, decodedCoverage[1]);
    //and
    assertEquals(decodedCovArrayString, bc, decodedCoverage[2]);
    //and
    assertEquals(decodedCovArrayString, bm,  decodedCoverage[3]);
  }

}
