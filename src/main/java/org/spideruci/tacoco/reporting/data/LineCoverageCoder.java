package org.spideruci.tacoco.reporting.data;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.internal.analysis.CounterImpl;

public class LineCoverageCoder {
  
  private final static int SHIFT = 8; 
  private final static int MASK = 0b11111111;
   
  public int encode(ILine lineCoverage) {
    ICounter insnCounter = lineCoverage.getInstructionCounter();
    int ic = insnCounter.getCoveredCount();
    int im = insnCounter.getMissedCount();
    ICounter branchCounter = lineCoverage.getBranchCounter();
    int bc = branchCounter.getCoveredCount();
    int bm = branchCounter.getMissedCount();
    
    int code = encode(ic, im, bc, bm); 
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
  
  public int toStatus(int[] counters) {
    int bm = counters[3];
    int bc = counters[2];
    int im = counters[1];
    int ic = counters[0];
    ICounter insnCounter = CounterImpl.getInstance(im, ic);
    ICounter branchCounter = CounterImpl.getInstance(bm, bc);
    return insnCounter.getStatus() | branchCounter.getStatus();
  }

}
