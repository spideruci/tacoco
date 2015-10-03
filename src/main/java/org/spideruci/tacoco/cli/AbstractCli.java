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
  public static final String PM = "tacoco.pm";
  public static final String THREAD = "tacoco.thread";
  
  //log level: off(default), on
  public static final String LOG = "tacoco.log";
  public static final String DB = "tacoco.db";
  public static final String OUTDIR = "tacoco.outdir";
  public static final String HOME = "tacoco.home";
  public static final String PROJECT = "tacoco.project";
  
  static final String PREFIX = "-D";
  
  protected static String readArgumentValue(String arg) {
    String value = System.getProperty(arg);
    return value;
  }
  
  public static String readOptionalArgumentValue(String arg, String defolt) {
    String value = readArgumentValue(arg);
    if(value == null) {
      return defolt;
    }
    return value;
  }
}