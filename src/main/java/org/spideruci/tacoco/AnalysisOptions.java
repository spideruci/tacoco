package org.spideruci.tacoco;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class AnalysisOptions {
	public static final String CP_ARG = "-cp:";
	public static final String AGENT = "analysis.agent";
	public static final String AGENT_ARGS = "analysis.agent.args";
	public static final String AGENT_MEM = "analysis.agent.mem";
	public static final String AGENT_XBOOTPATH = "analysis.agent.xboot";

	public static final String TEST_LISTENER = "analysis.test.listener";

	public static ArrayList<String> readOptions(File javaCliOptions) {
		ArrayList<String> options = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(javaCliOptions);

			while(scanner.hasNextLine()) {
				String option = scanner.nextLine();
				if(option == null)
					continue;

				option = option.trim();

				if(option.isEmpty()) { 
					continue;
				}

				if(option.startsWith("-")) {
					options.add(option);
				}
			}
			scanner.close();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return options;
	}

}
