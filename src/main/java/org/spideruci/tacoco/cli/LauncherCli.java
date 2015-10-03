package org.spideruci.tacoco.cli;

public class LauncherCli extends AbstractCli {
  
  public static String readArgumentValue(String arg) {
    String value = AbstractCli.readArgumentValue(arg);
    if(value == null) {
      printLauncherHelpForMissingArgError(arg);
    }
    return value;
  }
  
  public static void printLauncherHelpForMissingArgError(final String arg) {
    final String errorMessage = PREFIX + arg + " is a required argument.";
    printLauncherHelp(errorMessage);
  }
  
  public static void printLauncherHelp(final String errorMessage) {
    System.out.println(getHelpMenu(errorMessage));
    System.exit(0);
  }
  
  public static void printLauncherHelp() {
    printLauncherHelp(null);
  }
  
  public static String getHelpMenu(final String errorMessage) {
    throw new UnsupportedOperationException();
  }

}
