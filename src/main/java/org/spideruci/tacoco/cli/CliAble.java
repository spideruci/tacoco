package org.spideruci.tacoco.cli;

public interface CliAble {
  
  public static final String HELP = "tacoco.help";
  public static final String ROOT = "tacoco.root";
  public static final String SUT = "tacoco.sut";
  public static final String JSON = "tacoco.json";
  public static final String FMT = "tacoco.fmt";
  public static final String PP = "tacoco.pp";
  public static final String EXEC = "tacoco.exec";
  
  static final String PREFIX = "-D";
  
  public static class AnalyzerCli implements CliAble {
    public static String readArgumentValue(String arg) {
      String value = System.getProperty(arg);
      if(value == null) {
        printAnalyzerHelpForMissingArgError(arg);
      }
      return value;
    }
    
    public static String readOptionalArgumentValue(String arg, String def) {
      String value = System.getProperty(arg);
      if(value == null) {
        return def;
      }
      return value;
    }
    
    public static void printAnalyzerHelpForMissingArgError(final String arg) {
      final String errorMessage = PREFIX + arg + " is a required argument.";
      printAnalyzerHelpWithErrorMessage(errorMessage);
    }
    
    public static void printAnalyzerHelpWithErrorMessage(
        final String errorMessage) {
      System.out.println("ERROR! " + errorMessage);
      System.out.println("Refer to the following commandline arguments.");
      printAnalyzerHelp();
    }
    
    public static void printAnalyzerHelp() {
      final String helpMessage =
          "\nTacoco: Exec-file Analyzer\n"+
          "usage: mvn exec:java -q -Panalyzer [arguments] \n\nArguments:\n" +
              PREFIX + SUT + "=[dir]\t(Required) Absolute-path of system-under-test's root.\n" +
              PREFIX + EXEC + "=[*.exec]\t(Required) Absolute-path of input exec binary.\n" +
              PREFIX + JSON + "=[*.json]\t (Default: STDOUT) Absolute-path of per-test coverage output.\n" +
              PREFIX + FMT + "=[LOOSE|COMPACT|DENSE] (Default: DENSE) Compression format of coverage data.\n" +
              PREFIX + PP + "\tPretty prints coverage data to json file.\n" +
              PREFIX + HELP + "\tPrints this message and exits (with 0).\n";
      System.out.println(helpMessage);
      System.exit(0);
    }
  }
  
  public static class ReaderCli implements CliAble {
    
    public static String readArgumentValue(String arg) {
      String value = System.getProperty(arg);
      if(value == null) {
        printReaderHelpForMissingArgError(arg);
      }
      return value;
    }
    
    public static String readOptionalArgumentValue(String arg, String def) {
      String value = System.getProperty(arg);
      if(value == null) {
        return def;
      }
      return value;
    }
    
    public static void printReaderHelpForMissingArgError(final String arg) {
      final String errorMessage = arg + " is a required argument.";
      printReaderHelpWithErrorMessage(errorMessage);
    }
    
    public static void printReaderHelpWithErrorMessage(
        final String errorMessage) {
      System.out.println("ERROR:" + errorMessage);
      System.out.println("Refer to the following commandline arguments.\n");
      printReaderHelp();
    }
    
    public static void printReaderHelp() {
      final String helpMessage =
          "\nTacoco: Coverage Json-file Reader\n"+
          "usage: mvn exec:java -q -Preader [arguments] \n\nArguments:\n" +
              PREFIX + JSON + "=[*.json]\t(Required) Absolute-path of per-test coverage file.\n" +
              PREFIX + HELP + "\tPrints this message and exits (with 0).\n";
      System.out.println(helpMessage);
      System.exit(0);
    }
  }
  
  
  


}
