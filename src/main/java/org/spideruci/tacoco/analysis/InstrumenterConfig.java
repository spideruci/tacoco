package org.spideruci.tacoco.analysis;

public class InstrumenterConfig {
	private final String location;
	private final String arguments;
	private final String memory;
	private final String classPath;
	private final String xbootpath;
	private final boolean prependBootpath;

	private final static String JAVAAGENT = "-javaagent:";
	private final static String XBOOTCLASSPATH_P = "-Xbootclasspath/p:";
	private final static String XBOOTCLASSPATH_A = "-Xbootclasspath/a:";

	public static InstrumenterConfig get(String location, String arguments) {
		return get(location, arguments, null);
	}

	public static InstrumenterConfig get(String location, String arguments, String xbootpath) {
		return new InstrumenterConfig(location, arguments, null, xbootpath);
	}

	private InstrumenterConfig(String location, String arguments, String classPath, String xbootpath) {
		this.location = location;
		this.arguments = arguments;
		this.classPath = classPath;
		this.xbootpath = xbootpath;
		this.memory = "-Xmx1536M";
		this.prependBootpath = true;
	}

	public String buildJavaagentArg() {
		String agentOpt = JAVAAGENT;
		agentOpt += location;
		agentOpt += "=" + arguments;
		return agentOpt;
	}

	public String xbootclassPathArg() {
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
