package org.spideruci.tacoco;

public class InstrumenterConfig {

	private final String location;
	private final String arguments;
	private final String memory;
	private final String classPath;
	private final String xbootpath;
	private final boolean prependBootpath;

	private final static String JAVAAGENT = "-javaagent:";
	private final static String XBOOTCLASSPATH_P = "-xbootclasspath/p:";
	private final static String XBOOTCLASSPATH_A = "-xbootclasspath/a:";

	public static InstrumenterConfig get(String location, String arguments) {
		return new InstrumenterConfig(location, arguments, null, null);
	}

	private InstrumenterConfig(String location, String arguments, String classPath, String xbootpath) {
		this.location = location;
		this.arguments = arguments;
		this.classPath = classPath;
		this.xbootpath = xbootpath;
		this.memory = "-Xms2048M";
		this.prependBootpath = true;
	}

	public String buildJavaagentOpt() {
		String agentOpt = JAVAAGENT;
		agentOpt += location;
		agentOpt += "=" + arguments;
		return agentOpt;
	}

	public String xbootclassPathOpt() {
		String xbootpathOpt = 
				this.prependBootpath ? XBOOTCLASSPATH_P : XBOOTCLASSPATH_A;
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
