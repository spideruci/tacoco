package org.spideruci.tacoco.cli;

public class LauncherCli extends AbstractCli {

	public static String readArgumentValue(String arg) {
		return LANUCHER_CLI.readArgumentValueInternal(arg);
	}

	public static String readOptionalArgumentValue(String arg, String defolt) {
		return LANUCHER_CLI.readOptionalArgumentValueInternal(arg, defolt);
	}

	@Override
	public String getHelpMenu(final String errorMessage) {
		final String error = errorMessage == null ? null
				: ("ERROR! " + errorMessage + "\nRefer to the following commandline arguments.\n");
		final String white = "\t\t\t\t";
		final String helpMessage = "\nTacoco: Launcher\n"
				+ "usage: mvn exec:java -q -Plauncher [arguments]\n\nArguments:\n" 
				+ PREFIX + SUT+ "=<dir>\t\t(Required) Absolute-path of system-\n" 
				+ white + "under-test's root.\n"
				+ PREFIX + LISTENERS + "=<com.example.RunListener> ...\n"
				+ white + "Fully-qualified class name of the run listeners.\n"
				+ PREFIX + INST + "=<*.jar>\t\t(Required) Absolute path of jar\n"
				+ white + "reposnsible for instrumentation\n"
				+ white + "through a java-agent.\n"
				+ PREFIX + INST_ARGS + "=<agent args>\t(Optional) Arguments for the\n"
				+ white + "java agent in the instrumenter jar.\n"
				+ PREFIX + INST_MEM + "=<memory>\t(Default: -Xmx1536M) Runtime memory\n"
				+ white + "for the instrumenter.\n"
				+ PREFIX + INST_XBOOT + "=<classpath>\t(Optional) -Xbootclasspath/p option\n"
				+ white + "for the instrumenter.\n"
				+ PREFIX + HOME+"=<dir>\t\t(Defalult: current dir) Tacoco's Home dir\n"
				+ PREFIX + PROJECT + "=<string>\t(Default: ID from sut's build system)\n"
				+ white + "MAVEN: GroupID.ArtifactID\n"
				+ white + "All Tacoco's output files(exec, db, log, err)-\n"
				+ white + "use this for prefix.\n"
				+ PREFIX + THREAD + "=<number>\t(Default: 1) Number of JUnit Runner Thread\n" 
				+ PREFIX + LOG+"\t\t Prints Detailed Logs for Tacoco\n"
				+ PREFIX + OUTDIR+"=<dir>\t\t(Default: tacoco.home/tacoco_out)\n"
				+ PREFIX + DB+"\t\t\tDump exec output to sqlite3 db file\n"
				+ PREFIX + PIT+"\t\t\tRun PIT mutation test\n"
				+ PREFIX + NOJUNIT+"\t\t\tDo not run junit test\n"
				+ PREFIX + PITDB+"\t\t\tUpdate TacocoDB with PIT mutation info\n"
				+ PREFIX + HELP+ "\t\t\tPrints this message and exits (with 0).\n"
				;
		if (error != null) {
			return error + helpMessage;
		}
		return helpMessage;
	}

}
