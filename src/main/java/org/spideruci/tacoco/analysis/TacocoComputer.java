package org.spideruci.tacoco.analysis;

import java.util.ArrayList;
import java.util.List;

public class TacocoComputer extends AbstractRuntimeAnalyzer {
	
	@Override
	public void setup() {
		this.setBuildProbe();
		this.setupRuntimeListener();
	}
	
	@Override
	public void analyze() {
		List<String> klassesStrings = this.buildProbe.getTestClasses();
		List<Class<?>> klasses = new ArrayList<>();
		for(String klassString : klassesStrings) {
			Class<?> klass;
			try {
				klass = Class.forName(klassString);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			klasses.add(klass);
		}
		this.runTests(klasses);
	}

	@Override
	public String getName() {
		return "TACOCO";
	}

}
