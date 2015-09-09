package org.spideruci.tacoco;

import java.io.File;
import java.util.List;

public abstract class AbstractBuildProbe {
	
	public class Child{
		public String classpath;
		public String targetDir;
		public String[] jvmArgs;
		
		public Child(String cp, String dir, String[] args){
			classpath=cp;
			targetDir=dir;
			jvmArgs=args;
		}
	}
	
	public static enum BuilderType {MAVEN, ANT, GRADLE, TBD};
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
	
	private final static BuilderType detectBuilder(String AbsolutTargetPath){
		if(new File(AbsolutTargetPath, "pom.xml").exists()) return BuilderType.MAVEN;
		else if(new File(AbsolutTargetPath, "build.xml").exists()) return BuilderType.ANT;
		else if(new File(AbsolutTargetPath, "build.gradle").exists()) return BuilderType.GRADLE;
		return BuilderType.TBD;
	}
	
	public abstract List<String> getClasses();
	public abstract BuilderType getBuilderType();
	public abstract String getClasspath();
	public abstract boolean hasChild();
	public abstract List<Child> getChildren();
}
