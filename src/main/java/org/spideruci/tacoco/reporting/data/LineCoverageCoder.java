package org.spideruci.tacoco.reporting.data;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;

public class LineCoverageCoder {
  
  private final static int SHIFT = 8; 
  private final static int MASK = 0b11111111;
   
  int encode(ILine lineCoverage) {
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
  
  int[] decode(int code) {
    int bm = code & MASK;
    code = code >>> SHIFT;
    int bc = code & MASK;
    code = code >>> SHIFT;
    int im = code & MASK;
    code = code >>> SHIFT;
    int ic = code & MASK;
    
    return new int[] {ic, im, bc, bm};
  }

}
