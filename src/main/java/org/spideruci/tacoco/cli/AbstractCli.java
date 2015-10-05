package org.spideruci.tacoco.cli;

public abstract class AbstractCli {
  
  public static final String HELP = "tacoco.help";
  public static final String ROOT = "tacoco.root";
  public static final String SUT = "tacoco.sut";
  public static final String JSON = "tacoco.json";
  public static final String OUT = "tacoco.out";
  public static final String FMT = "tacoco.fmt";
  public static final String PP = "tacoco.pp";
  public static final String EXEC = "tacoco.exec";
  
  //parallel execution mode: none(default), class, method, both
  public static final String THREAD = "tacoco.thread";
  
  //log level: off(default), on
  public static final String LOG = "tacoco.log";
  public static final String DB = "tacoco.db";
  public static final String OUTDIR = "tacoco.outdir";
  public static final String HOME = "tacoco.home";
  public static final String PROJECT = "tacoco.project";
  
  public static final String INST = "tacoco.inst";
  public static final String INST_ARGS = "tacoco.inst.arg";
  public static final String INST_MEM = "tacoco.inst.mem";
  public static final String INST_XBOOT = "tacoco.inst.xboot";
  
  static final String PREFIX = "-D";
  
  public static final AnalyzerCli ANALYZER_CLI = new AnalyzerCli();
  public static final ReaderCli READER_CLI = new ReaderCli();
  public static final LauncherCli LANUCHER_CLI = new LauncherCli();
  
  protected String readOptionalArgumentValueInternal(String arg, String defolt) {
    String value = System.getProperty(arg);
    if(value == null) {
      return defolt;
    }
    return value;
  }
  
  protected String readArgumentValueInternal(String arg) {
    String value = System.getProperty(arg);
    if(value == null) {
      printHelpForMissingArgError(arg);
    }
    return value;
  }
  
  public void printHelpForMissingArgError(final String arg) {
    final String errorMessage = PREFIX + arg + " is a required argument.";
    printHelp(errorMessage);
  }
  
  public void printHelp() {
    printHelp(null);
  }
  
  public void printHelp(final String errorMessage) {
    System.out.println(getHelpMenu(errorMessage));
    System.exit(0);
  }
  
  public abstract String getHelpMenu(final String errorMessage);
}