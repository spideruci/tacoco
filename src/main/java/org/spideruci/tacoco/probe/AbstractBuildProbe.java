package org.spideruci.tacoco.probe;

import java.io.File;
import java.util.List;

public abstract class AbstractBuildProbe {
	
	public static enum BuilderType {MAVEN, ANT, GRADLE, TBD};
	private static AbstractBuildProbe instance = null;
	
	public final static AbstractBuildProbe getInstance(final String absoluteTargetPath) {

		switch (detectBuilder(absoluteTargetPath)) {
			case MAVEN:
				instance = new MavenBuildProbe(absoluteTargetPath);
				break;
			case ANT:
				instance = new AntBuildProbe(absoluteTargetPath);
				break;
			case GRADLE:
				instance = new GradleBuildProbe(absoluteTargetPath);
				break;
			default:
				break;
		}

		return instance;
	}

	private final static BuilderType detectBuilder(final String absoluteTargetPath) {
		if (new File(absoluteTargetPath, "pom.xml").exists()) {
			return BuilderType.MAVEN;
		}
		
		if (new File(absoluteTargetPath, "build.xml").exists()) {
			return BuilderType.ANT;
		} 
		
		if (new File(absoluteTargetPath, "build.gradle").exists()) {
			return BuilderType.GRADLE;
		}

		return BuilderType.TBD;
	}
	
	public abstract String getAbsoluteTargetPath();
	public abstract List<String> getTestClasses();
	public abstract List<String> getClasses();
	public abstract BuilderType getBuilderType();
	public abstract String getClasspath();
	public abstract boolean hasChild();
	public abstract String getId();
	public abstract List<String> getClassDirs();
	public abstract List<String> getTestClassDirs();
}
