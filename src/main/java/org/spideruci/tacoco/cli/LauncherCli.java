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
				+ PREFIX + HOME+"=<dir>\t\t(Defalult: current dir) Tacoco's Home dir\n"
				+ PREFIX + PROJECT + "=<string>\t(Default: ID from sut's build system)\n"
				+ white + "MAVEN: GroupID.ArtifactID\n"
				+ white + "All Tacoco's output files(exec, db, log, err)-\n"
				+ white + "use this for prefix.\n"
				+ PREFIX + THREAD + "=<number>\t(Default: 1) Number of JUnit Runner Thread\n" 
				+ PREFIX + LOG+"=<on/off>\t\t(Default: off) Print Detailed Logs\n"
				+ PREFIX + OUTDIR+"=<dir>\t\t(Default: tacoco.home/tacoco_out)\n"
				+ PREFIX + DB+"\t\t\tDump exec output to sqlite3 db file\n"
				+ PREFIX + HELP+ "\t\t\tPrints this message and exits (with 0).\n"
				;
		if (error != null) {
			return error + helpMessage;
		}
		return helpMessage;
	}

}
