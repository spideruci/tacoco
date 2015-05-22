package org.spideruci.tacoco.reporting.data;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.internal.analysis.CounterImpl;

public class LineCoverageCoder {
  
  private final static int SHIFT = 8; 
  private final static int MASK = 0b11111111;
   
  public int encode(ILine lineCoverage) {
    int code = 0;
    ICounter insnCounter = lineCoverage.getInstructionCounter();
    code = code | insnCounter.getCoveredCount();
    code = code << SHIFT;
    code = code | insnCounter.getMissedCount();
    code = code << SHIFT;
    ICounter branchCounter = lineCoverage.getBranchCounter();
    code = code | branchCounter.getCoveredCount();
    code = code << SHIFT;
    code = code | branchCounter.getMissedCount();
    return code;
  }
  
  public int encode(int ic, int im, int bc, int bm) {
    int code = 0;
    code = code | ic;
    code = code << SHIFT;
    code = code | im;
    code = code << SHIFT;
    code = code | bc;
    code = code << SHIFT;
    code = code | bm;
    return code;
  }
  
  public int[] decode(int code) {
    int bm = code & MASK;
    code = code >>> SHIFT;
    int bc = code & MASK;
    code = code >>> SHIFT;
    int im = code & MASK;
    code = code >>> SHIFT;
    int ic = code & MASK;
    
    return new int[] {ic, im, bc, bm};
  }
  
  public int decodeStatus(int[] counters) {
    int bm = counters[3];
    int bc = counters[2];
    int im = counters[1];
    int ic = counters[0];
    ICounter insnCounter = CounterImpl.getInstance(im, ic);
    ICounter branchCounter = CounterImpl.getInstance(bm, bc);
    return insnCounter.getStatus() | branchCounter.getStatus();
  }

}
