package org.spideruci.tacoco;

import java.io.File;
import java.util.ArrayList;

public abstract class AbstractBuildProbe {
	
	private static enum builder {MAVEN, ANT, GRADLE, TBD};
	private static AbstractBuildProbe instance = null;
	
	public final static AbstractBuildProbe getInstance(String AbsolutTargetPath){
		//if(instance != null) return instance;
		
		switch(detectBuilder(AbsolutTargetPath)){
		case MAVEN:
			instance = new MavenBuildProbe(AbsolutTargetPath);
			break;
		case ANT:
			instance = new AntBuildProbe(AbsolutTargetPath);
			break;
		case GRADLE:
			instance = new GradleBuildProbe(AbsolutTargetPath);
			break;
		}
		
		return instance;
	}
	
	private final static builder detectBuilder(String AbsolutTargetPath){
		
		if(new File(AbsolutTargetPath, "pom.xml").exists()) return builder.MAVEN;
		else if(new File(AbsolutTargetPath, "build.xml").exists()) return builder.ANT;
		else if(new File(AbsolutTargetPath, "build.gradle").exists()) return builder.GRADLE;
		return builder.TBD;
	}
	
	public abstract ArrayList<String> getClasses();
	public abstract String getBuilderName();
	public abstract String getClasspath() throws Exception;
}
