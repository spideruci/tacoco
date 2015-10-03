package org.spideruci.tacoco.cli;

public class AnalyzerCli extends AbstractCli {
  
  public static String readArgumentValue(String arg) {
    String value = AbstractCli.readArgumentValue(arg);
    if(value == null) {
      printAnalyzerHelpForMissingArgError(arg);
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