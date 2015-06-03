package org.spideruci.tacoco.cli;

public interface CliAble {
  
  public static final String HELP = "tacoco.help";
  public static final String ROOT = "tacoco.root";
  public static final String SUT = "tacoco.sut";
  public static final String JSON = "tacoco.json";
  public static final String OUT = "tacoco.out";
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
      printAnalyzerHelp(errorMessage);
    }
    
    public static void printAnalyzerHelp() {
      printAnalyzerHelp(null);
    }
    
    public static void printAnalyzerHelp(final String errorMessage) {
      System.out.println(getHelpMenu(errorMessage));
      System.exit(0);
    }
    
    public static String getHelpMenu(final String errorMessage) {
      final String error = errorMessage == null ? null :
          ("ERROR! " + errorMessage + 
          "\nRefer to the following commandline arguments.\n"); 
      final String white = "                                    ";
      final String helpMessage =
          "\nTacoco: Exec-file Analyzer\n"+
          "usage: mvn exec:java -q -Panalyzer [arguments]\n\nArguments:\n" +
              PREFIX + SUT + "=<dir>                  (Required) Absolute-path of system-\n"
                  + white + "under-test's root.\n" +
              PREFIX + EXEC + "=<*.exec>              (Required) Absolute-path of input exec\n"
                  + white + "binary.\n" +
              PREFIX + JSON + "=<*.json>              (Default: STDOUT) Absolute-path of per-test\n"
                  + white + "coverage output.\n" +
              PREFIX + FMT + "=<LOOSE|COMPACT|DENSE>  (Default: DENSE) Compression format of\n"
                  + white + "coverage data.\n" +
              PREFIX + PP + "                         Pretty prints coverage data to json file.\n" +
              PREFIX + HELP + "                       Prints this message and exits (with 0).\n";
      
      if(error != null) {
        return error + helpMessage; 
      }
      return helpMessage;
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
      final String errorMessage = PREFIX + arg + " is a required argument.";
      printReaderHelp(errorMessage);
    }
    
    public static void printReaderHelp(final String errorMessage) {
      System.out.println(getHelpMenu(errorMessage));
      System.exit(0);
    }
    
    public static void printReaderHelp() {
      printReaderHelp(null);
    }
    
    public static String getHelpMenu(final String errorMessage) {
      final String error = errorMessage == null ? null :
          ("ERROR! " + errorMessage + 
          "\nRefer to the following commandline arguments.\n"); 
      final String helpMessage =
          "\nTacoco: Coverage Json-file Reader\n"+
          "usage: mvn exec:java -q -Preader [arguments]\n\nArguments:\n" +
              PREFIX + JSON + "=<*.json>  (Required) Absolute-path of per-test coverage file.\n" +
              PREFIX + OUT + "=<*.json>   Absolute-path of per-sourcefile coverage matrix.\n" +
              PREFIX + PP + "             Pretty prints coverage data to json file.\n" +
              PREFIX + HELP + "           Prints this message and exits (with 0).\n";
      
      if(error != null) {
        return error + helpMessage; 
      }
      return helpMessage;
    }
  }
}