package org.spideruci.tacoco.reporting.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.spideruci.hamcrest.primitive.IsIntArrayContaining.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jacoco.core.analysis.ILine;
import org.junit.Test;

public class TestLinesStatusCoder {

  @Test
  public void shouldCodeCoverageStatusForZerolinesWithZeroIntegers() {
    //given
    int numberOfLines = 0;
    int expectedCodeCount = 0;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusForUnder16linesInOneInteger() {
    //given
    int numberOfLines = 8;
    int expectedCodeCount = 1;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusFor15linesInOneInteger() {
    //given
    int numberOfLines = 15;
    int expectedCodeCount = 1;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);
    //and
    assertThat(linesCoverage.size(), equalTo(numberOfLines));

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusFor16linesInOneInteger() {
    //given
    int numberOfLines = 16;
    int expectedCodeCount = 1;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);
    //and
    assertThat(linesCoverage.size(), equalTo(numberOfLines));

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusForOver16AndUnder32linesInTwoIntegers() {
    //given
    int numberOfLines = 17;
    int expectedCodeCount = 2;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusForOver31linesInTwoIntegers() {
    //given
    int numberOfLines = 31;
    int expectedCodeCount = 2;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusForOver32linesInTwoIntegers() {
    //given
    int numberOfLines = 32;
    int expectedCodeCount = 2;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldCodeCoverageStatusForOver33linesInThreeIntegers() {
    //given
    int numberOfLines = 33;
    int expectedCodeCount = 3;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);

    //then
    assertEquals(expectedCodeCount, codes.length);
  }

  @Test
  public void shouldDecode5LineStatusesToOriginalStatusesAndTrailingZeros() {
    //given
    int numberOfLines = 5;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    int[] originalStatuses = new int[numberOfLines];
    int index = 0;

    for(ILine lineCoverage : linesCoverage) {
      originalStatuses[index] = lineCoverage.getStatus();
      index += 1;
    }

    LinesStatusCoder coder = new LinesStatusCoder();


    //when
    int[] codes = coder.encode(linesCoverage);
    //and
    int[] actualStatuses = coder.decode(codes);
    //and
    int[] truncatedActualStatuses = Arrays.copyOf(actualStatuses, numberOfLines);
    //and
    int[] trailingStatuses = Arrays.copyOfRange(actualStatuses, numberOfLines, actualStatuses.length);

    //then
    assertArrayEquals(originalStatuses, truncatedActualStatuses);
    //and
    assertEquals(actualStatuses.length - numberOfLines, trailingStatuses.length);
    //and
    assertThat(trailingStatuses, hasInt(0));
    assertThat(trailingStatuses, hasInt(not(1)));
    assertThat(trailingStatuses, hasInt(not(2)));
    assertThat(trailingStatuses, hasInt(not(3)));
  }

  @Test
  public void shouldDecode15LineStatusesToOriginalStatusesAndOneTrailingZero() {
    //given
    int numberOfLines = 15;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    int[] originalStatuses = new int[numberOfLines];
    int index = 0;

    for(ILine lineCoverage : linesCoverage) {
      originalStatuses[index] = lineCoverage.getStatus();
      index += 1;
    }

    LinesStatusCoder coder = new LinesStatusCoder();


    //when
    int[] codes = coder.encode(linesCoverage);
    //and
    int[] actualStatuses = coder.decode(codes);
    //and
    int[] truncatedActualStatuses = Arrays.copyOf(actualStatuses, numberOfLines);
    //and
    int[] trailingStatuses = Arrays.copyOfRange(actualStatuses, numberOfLines, actualStatuses.length);

    //then
    assertArrayEquals(originalStatuses, truncatedActualStatuses);
    //and
    assertEquals(actualStatuses.length - numberOfLines, trailingStatuses.length);
    //and
    assertEquals(1, trailingStatuses.length);
    //and
    assertThat(trailingStatuses, hasInt(0));
    assertThat(trailingStatuses, hasInt(not(1)));
    assertThat(trailingStatuses, hasInt(not(2)));
    assertThat(trailingStatuses, hasInt(not(3)));
  }
  
  @Test
  public void shouldDecode16LineStatusesToOriginalStatusesAndNoTrailingZeros() {
    //given
    int numberOfLines = 16;
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    int[] originalStatuses = new int[numberOfLines];
    int index = 0;

    for(ILine lineCoverage : linesCoverage) {
      originalStatuses[index] = lineCoverage.getStatus();
      index += 1;
    }

    LinesStatusCoder coder = new LinesStatusCoder();


    //when
    int[] codes = coder.encode(linesCoverage);
    //and
    int[] actualStatuses = coder.decode(codes);
    //and
    int[] truncatedActualStatuses = Arrays.copyOf(actualStatuses, numberOfLines);
    //and
    int[] trailingStatuses = Arrays.copyOfRange(actualStatuses, numberOfLines, actualStatuses.length);

    //then
    assertArrayEquals(originalStatuses, truncatedActualStatuses);
    //and
    assertEquals(actualStatuses.length - numberOfLines, trailingStatuses.length);
    //and
    assertEquals(0, trailingStatuses.length);
  }
  
  @Test
  public void shouldDecode17LineStatusesToOriginalStatusesAnd15TrailingZeros() {
    //given
    int numberOfLines = 17; int index = 0;
    int[] originalStatuses = new int[numberOfLines];
    //and
    Collection<ILine> linesCoverage = mockLinesCoverage(numberOfLines);
    //and
    for(ILine lineCoverage : linesCoverage) {
      originalStatuses[index] = lineCoverage.getStatus();
      index += 1;
    }
    //and
    LinesStatusCoder coder = new LinesStatusCoder();

    //when
    int[] codes = coder.encode(linesCoverage);
    //and
    int[] actualStatuses = coder.decode(codes);
    //and
    int[] truncatedActualStatuses = Arrays.copyOf(actualStatuses, numberOfLines);
    //and
    int[] trailingStatuses = Arrays.copyOfRange(actualStatuses, numberOfLines, actualStatuses.length);

    //then
    assertArrayEquals(originalStatuses, truncatedActualStatuses);
    //and
    assertEquals(actualStatuses.length - numberOfLines, trailingStatuses.length);
    //and
    assertEquals(15, trailingStatuses.length);
    //and
    assertThat(trailingStatuses, hasInt(0));
    assertThat(trailingStatuses, hasInt(not(1)));
    assertThat(trailingStatuses, hasInt(not(2)));
    assertThat(trailingStatuses, hasInt(not(3)));
  }

  private Collection<ILine> mockLinesCoverage(int lineCount) {
    ArrayList<ILine> lines = new ArrayList<>();
    int[] statuses = new int[] {0, 1, 2, 3};
    for(int i = 0; i < lineCount; i += 1) {
      ILine lineCoverage = mock(ILine.class);
      when(lineCoverage.getStatus()).thenReturn(statuses[i%4]);
      lines.add(lineCoverage);
    }
    return lines;
  }

}
