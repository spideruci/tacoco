package org.spideruci.tacoco.cli;

public class ReaderCli extends AbstractCli {
  
  public static String readArgumentValue(String arg) {
    String value = AbstractCli.readArgumentValue(arg);
    if(value == null) {
      printReaderHelpForMissingArgError(arg);
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