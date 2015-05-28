package org.spideruci.tacoco.reporting;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.google.gson.stream.JsonReader;

public class TestStartReadingTestCase {

  @Test
  public void shouldStartReadingEmptyArray() {
    //given
    JsonReader reader = new JsonReader(new StringReader("[]"));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.startReadingTestCase();
    } catch (IOException e) {
      //then
      e.printStackTrace();
      fail("Did not expect IOException");
      return;
    }
    
    //then pass!
    return;
  }
 
  @Test
  public void shouldThrowEOFExceptionWhenReadingArrayStart() {
    //given
    JsonReader reader = new JsonReader(new StringReader("["));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.startReadingTestCase();
    } catch (IOException e) {
      //then
      e.printStackTrace();
      fail("Did not expect IOException");
      return;
    }
    
    //or, then pass!
    return;
  }
  
  @Test
  public void shouldThrowExceptionWhenReadingObjectStart() {
    //given
    JsonReader reader = new JsonReader(new StringReader("{"));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.startReadingTestCase();
    } catch(IllegalStateException e) {
      // then
      assertThat(e.getMessage(), startsWith("Expected BEGIN_ARRAY but was BEGIN_OBJECT"));
      return;
    } catch (IOException e) {
      // or then
      e.printStackTrace();
      fail("Did not expect IOException");
      return;
    }
    
    //or, then
    fail("Expected an IllegalStateException to be thrown");
  }
  
  @Test
  public void shouldThrowEOFExceptionWhenReadingSingleSpace() {
    //given
    JsonReader reader = new JsonReader(new StringReader(" "));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);

    // when
    try {
      coverageReader.startReadingTestCase();
    } catch (EOFException e) {
      // then
      assertThat(e.getMessage(), equalTo("End of input at line 1 column 2"));
      return;
    } catch (IOException e) {
      // or, then
      e.printStackTrace();
      fail("Did not expect IOException");
      return;
    }
    
    // or, then
    fail("Expected an EOFException to be thrown");
  }
  
  @Test
  public void shouldThrowEOFExceptionWhenReadingSingleTab() {
    //given
    JsonReader reader = new JsonReader(new StringReader("\t"));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);

    // when
    try {
      coverageReader.startReadingTestCase();
    } catch (EOFException e) {
      // then
      assertThat(e.getMessage(), equalTo("End of input at line 1 column 2"));
      return;
    } catch (IOException e) {
      // or, then
      e.printStackTrace();
      fail("Did not expect IOException");
      return;
    }
    
    // or, then
    fail("Expected an EOFException to be thrown");
  }

}
