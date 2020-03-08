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
	public static final String PIT = "tacoco.pit";
	public static final String PITDB = "tacoco.pitdb";
	public static final String NOJUNIT = "tacoco.nojunit";

	public static final String INST = "tacoco.inst";
	public static final String INST_ARGS = "tacoco.inst.arg";
	public static final String INST_MEM = "tacoco.inst.mem";
	public static final String INST_XBOOT = "tacoco.inst.xboot";

	//public static final String LISTENER = "tacoco.listener";
	public static final String LISTENERS = "tacoco.listeners";
	public static final String ANALYZER = "tacoco.analyzer";
	public static final String ANALYZER_OPTS = "analyzer.opts";

	static final String PREFIX = "-D";
	
	
	public static final String PIT_NO_UNIT_TEST = "tacoco.pit.no_unittest_if_result_exist";
	public static final String PIT_JAR = "tacoco.pit.jar";
	public static final String PIT_MAX_MUTATIONS_PER_CLASS = "tacoco.pit.maxMutationsPerClass";
	public static final AnalyzerCli ANALYZER_CLI = new AnalyzerCli();
	public static final ReaderCli READER_CLI = new ReaderCli();
	public static final LauncherCli LAUNCHER_CLI = new LauncherCli();
	
	public static String arg(String argName) {
		return PREFIX + argName;
	}

	public static String argEquals(String argName, String value) {
		return arg(argName) + "=" + value;
	}

	public static boolean readBooleanArgument(String arg) {
		return System.getProperties().containsKey(arg);
	}

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