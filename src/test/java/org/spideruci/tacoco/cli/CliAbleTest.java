package org.spideruci.tacoco.cli;

import
 static org.junit.Assert.*;

import org.junit.Test;

public class CliAbleTest {
  
  @Test
  public void analyzerHelpShouldMatchArgumentMenu() {
    //given
    String argMenu = getAnalyzerArgumentMenu();
    //when
    String helpMenu = CliAble.AnalyzerCli.getHelpMenu(null);
    //then
    assertEquals(argMenu, helpMenu);
  }
  
  @Test
  public void analyzerHelpShouldMatchArgumentMenuWithSomeErrorMessage() {
    //given
    String errorMessage = "some random error";
    String argMenu = getAnalyzerArgumentMenu(errorMessage);
    //when
    String helpMenu = CliAble.AnalyzerCli.getHelpMenu(errorMessage);
    //then
    assertEquals(argMenu, helpMenu);
  }

  private static String getAnalyzerArgumentMenu(String errorMessage) {    
    final String menu =
        "ERROR! " + errorMessage + 
        "\nRefer to the following commandline arguments.\n" +
        getAnalyzerArgumentMenu();
    return menu;
  }
  
  private static String getAnalyzerArgumentMenu() {    
    final String menu =
        "\nTacoco: Exec-file Analyzer\n" +
            "usage: mvn exec:java -q -Panalyzer [arguments]\n" + 
            "\n" +
            "Arguments:\n" +
            "-Dtacoco.sut=<dir>                  (Required) Absolute-path of system-\n" +
            "                                    under-test's root.\n" +
            "-Dtacoco.exec=<*.exec>              (Required) Absolute-path of input exec\n" +
            "                                    binary.\n" +
            "-Dtacoco.json=<*.json>              (Default: STDOUT) Absolute-path of per-test\n" +
            "                                    coverage output.\n" +
            "-Dtacoco.fmt=<LOOSE|COMPACT|DENSE>  (Default: DENSE) Compression format of\n" +
            "                                    coverage data.\n" +
            "-Dtacoco.pp                         Pretty prints coverage data to json file.\n" +
            "-Dtacoco.help                       Prints this message and exits (with 0).\n";
    return menu;
  }
  
  @Test
  public void readerHelpShouldMatchArgumentMenu() {
    //given
    String argMenu = getReaderArgumentMenu();
    //when
    String helpMenu = CliAble.ReaderCli.getHelpMenu(null);
    //then
    assertEquals(argMenu, helpMenu);
  }
  
  @Test
  public void readerHelpShouldMatchArgumentMenuWithSomeErrorMessage() {
    //given
    String errorMessage = "some random error";
    String argMenu = getReaderArgumentMenu(errorMessage);
    //when
    String helpMenu = CliAble.ReaderCli.getHelpMenu(errorMessage);
    //then
    assertEquals(argMenu, helpMenu);
  }
  
  private static String getReaderArgumentMenu(String errorMessage) {    
    final String menu =
        "ERROR! " + errorMessage + 
        "\nRefer to the following commandline arguments.\n" +
        getReaderArgumentMenu();
    return menu;
  }
  
  private static String getReaderArgumentMenu() {    
    final String menu =
        "\nTacoco: Coverage Json-file Reader\n" +
            "usage: mvn exec:java -q -Preader [arguments]\n" + 
            "\n" +
            "Arguments:\n" +
            "-Dtacoco.json=<*.json>  (Required) Absolute-path of per-test coverage file.\n" +
            "-Dtacoco.out=<*.json>   Absolute-path of per-sourcefile coverage matrix.\n" +
            "-Dtacoco.pp             Pretty prints coverage data to json file.\n" +
            "-Dtacoco.help           Prints this message and exits (with 0).\n";
    return menu;
  }
}
