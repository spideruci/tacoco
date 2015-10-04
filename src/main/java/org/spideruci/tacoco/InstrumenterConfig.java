package org.spideruci.tacoco;

public class InstrumenterConfig {
  
  private final String path;
  private final String name;
  private final String arguments;
  private final String memory;
  private final String classPath;
  private final String xbootpath;
  private final boolean prependBootpath;
  
  private final static String JAVAAGENT = "-javaagent:";
  private final static String XBOOTCLASSPATH = "-xbootclasspath/p:";
  
  public static InstrumenterConfig get(String name, String path, String arguments) {
    return new InstrumenterConfig(name, path, arguments, null, null);
  }
  
  private InstrumenterConfig(String name, String path, String arguments, String classPath, String xbootpath) {
    this.name = name;
    this.path = path;
    this.arguments = arguments;
    this.classPath = classPath;
    this.xbootpath = xbootpath;
    this.memory = "-Xmx1536M";
    this.prependBootpath = true;
  }
  
  public String buildJavaagentOpt() {
    String agentOpt = JAVAAGENT;
    agentOpt += path + name;
    agentOpt += "=" + arguments;
    return agentOpt;
  }
  
  public String xbootclassPathOpt() {
    String xbootpathOpt = XBOOTCLASSPATH;
    if(!this.prependBootpath) {
      xbootpathOpt.replace("/p", "/a");
    }
    xbootpathOpt += xbootpath;
    return xbootpathOpt;
  }
  
  public String getClassPath() {
    return this.classPath;
  }
  
  public String getMemory() {
    return this.memory;
  }
}
