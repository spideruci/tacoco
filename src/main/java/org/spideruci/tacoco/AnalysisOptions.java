package org.spideruci.tacoco;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import com.esotericsoftware.yamlbeans.YamlReader;

public class AnalysisOptions {
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
				if(option == null 
						|| option.isEmpty() 
						|| !option.startsWith("-")) {
					continue;
				}
				
				options.add(option);
			}
			scanner.close();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return options;
	}

}
