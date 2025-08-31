package org.spideruci.tacoco.probe;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.spideruci.tacoco.module.GradleModule;
import org.spideruci.tacoco.util.PathBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GradleBuildProbe extends AbstractBuildProbe {

	final private String targetDir;
	private List<GradleModule> submodules;

	public GradleBuildProbe(String absoluteTargetPath) {
		targetDir = absoluteTargetPath;
		submodules = getSubmodules(absoluteTargetPath);
	}

	public String getAbsoluteTargetPath() {
		return this.targetDir;
	}

	@Override
	public List<String> getTestClasses() {
		List<String> testClasses = new ArrayList<>();
		for (GradleModule module : this.submodules) {
			testClasses.addAll(module.getTestClasses());
		}
		return testClasses;
	}

	@Override
	public BuilderType getBuilderType() {
		return BuilderType.GRADLE;
	}

	@Override
	public String getClasspath() {
		StringBuilder sb = new StringBuilder();
		for (GradleModule module : this.submodules) {
			sb.append(module.getClasspath() + File.pathSeparator);
		}
		return sb.toString();
	}

	@Override
	public boolean hasChild() {
		EclipseProject eclipseProject = getGradleModel(targetDir);
		return eclipseProject != null && !eclipseProject.getChildren().isEmpty();
	}

	@Override
	public String getId() {
		EclipseProject eclipseProject = getGradleModel(targetDir);
		return eclipseProject.getName();
	}

	@Override
	public List<String> getClasses() {
		List<String> classes = new ArrayList<>();
		for (GradleModule module : this.submodules) {
			classes.addAll(module.getClasses());
		}
		return classes;
	}

	@Override
	public List<String> getClassDirs() {
		List<String> classDirs = new ArrayList<>();
		for (GradleModule module : this.submodules) {
			classDirs.add(module.getClassDir());
		}
		return classDirs;
	}

	@Override
	public List<String> getTestClassDirs() {
		List<String> testClassDirs = new ArrayList<>();
		for (GradleModule module : this.submodules) {
			testClassDirs.add(module.getTestclassDir());
		}
		return testClassDirs;
	}

	private List<GradleModule> getSubmodules(final String absoluteTargetPath) {
		final List<GradleModule> modules = new ArrayList<>();
		GradleModule rootModule = new GradleModule(absoluteTargetPath);
		modules.add(rootModule);

		EclipseProject eclipseProject = getGradleModel(absoluteTargetPath);
		if (eclipseProject != null && !eclipseProject.getChildren().isEmpty()) {
			for (EclipseProject childProject : eclipseProject.getChildren()) {
				String childDir = new PathBuilder().path(this.targetDir).path(childProject.getName()).buildFilePath();
				modules.add(new GradleModule(childDir));
			}
		}

		return modules;
	}

	private EclipseProject getGradleModel(final String absoluteTargetPath) {

		GradleConnector connector = GradleConnector.newConnector();
		File target = new File(absoluteTargetPath);
		connector.forProjectDirectory(target);
		ProjectConnection connection = null;
		EclipseProject eclipseProject = null;
		try {
			connection = connector.connect();

			eclipseProject = connection.getModel(EclipseProject.class);

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return eclipseProject;
	}

}
