package org.spideruci.tacoco.reporting;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.spideruci.tacoco.reporting.CoverageJsonReader;

import com.google.gson.Gson;

@RunWith(Parameterized.class)
public class TestDoubleObjectReader {
  
  @Parameters
  public static Collection<Object[]> data() {
    ArrayList<Object[]> integers = new ArrayList<>();
    int[] array = new int[] {0, 1, 2, -1, -2, -3, 100, 200, -100, -200, 
        Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 10, 
        Integer.MIN_VALUE, Integer.MIN_VALUE + 1,Integer.MIN_VALUE + 10};
    for(int i : array ) {
      integers.add(new Object[] { i });
    }
    
    return integers;
  }
  
  private int x;
  
  public TestDoubleObjectReader(int num) {
    x = num;
  }

  @Test
  public void shouldReadOriginalIntAfterDeserializationAsObject() {
    //given
    Object obj = (Object) x;
    String json = new Gson().toJson(obj);
    CoverageJsonReader reader = new CoverageJsonReader(null);
    
    //when
    Object fromJson = new Gson().fromJson(json, Object.class);
    int y = reader.readDoubleObjectAsInt(fromJson);
    
    //then
    assertEquals(x, y);
  }
  
  @Test
  public void shouldReadOriginalIntArrayElementAfterDeserializationAsObject() {
    //given
    Object obj = new Object[] { (Object) x };
    String json = new Gson().toJson(obj);
    CoverageJsonReader reader = new CoverageJsonReader(null);
    
    //when
    Object[] fromJson = new Gson().fromJson(json, Object[].class);
    Object element = fromJson[0];
    int y = reader.readDoubleObjectAsInt(element);
    
    //then
    assertEquals(x, y);
  }

}
