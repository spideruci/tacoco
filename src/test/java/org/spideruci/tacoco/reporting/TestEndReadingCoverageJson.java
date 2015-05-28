package org.spideruci.tacoco.reporting;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;

public class TestEndReadingCoverageJson {

  @Test
  public void shouldEndReadingEmptyArray() {
    //given
    JsonReader reader = new JsonReader(new StringReader("[]"));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.startReading();
      coverageReader.endReading();
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
  public void shouldThrowMalformedJsonException_WithOnlyArrayEnd_WithoutArrayStart() {
    //given
    JsonReader reader = new JsonReader(new StringReader("]"));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.endReading();
    } catch(MalformedJsonException e) {
      // then
      assertThat(e.getMessage(), startsWith("Unexpected value"));
      return;
    } catch (IOException e) {
      //then
      e.printStackTrace();
      fail("Did not expect IOException");
      return;
    }
    
    //or, then pass!
    fail("Expected MalformedException");
  }
  
  @Test
  public void shouldThrowExceptionWhenReadingObjectStart() {
    //given
    JsonReader reader = new JsonReader(new StringReader("{"));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.endReading();
    } catch(IllegalStateException e) {
      // then
      assertThat(e.getMessage(), startsWith("Expected END_ARRAY but was BEGIN_OBJECT"));
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
  public void shouldThrowExceptionWhenReadingArrayStart() {
    //given
    JsonReader reader = new JsonReader(new StringReader("["));
    CoverageJsonReader coverageReader = new CoverageJsonReader(reader);
    
    //when
    try {
      coverageReader.endReading();
    } catch(IllegalStateException e) {
      // then
      assertThat(e.getMessage(), startsWith("Expected END_ARRAY but was BEGIN_ARRAY"));
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
      coverageReader.endReading();
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
      coverageReader.endReading();
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
