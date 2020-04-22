package org.spideruci.tacoco.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.spideruci.tacoco.util.PathBuilder;

import org.apache.maven.shared.invoker.*;

public class MavenModule extends AbstractModule {

	public MavenModule(String targetDir) {
		this.targetDir = targetDir;
		this.classpath = null;
	}

	@Override
	public List<String> getTestClasses() {
		List<String> ret = new ArrayList<>();
		DirectoryScanner scanner = new DirectoryScanner();
		String baseDir = this.targetDir + "/target/classes";
		if (!new File(baseDir).exists()) {
			return ret;
		}
		makeFilter(scanner);
		final String testClassPath = new PathBuilder().path(targetDir).path("target").path("test-classes")
				.buildFilePath(); // MAVEN TEST CLASS FOLDER
		if (!new File(testClassPath).exists()) {
			return ret;
		}
		scanner.setBasedir(testClassPath);
		scanner.setCaseSensitive(true);
		scanner.scan();

		for (String s : scanner.getIncludedFiles()) {
			ret.add(s.replaceAll("/", ".").replaceAll("\\\\", ".").replaceAll("\\.class", ""));
		}
		return ret;

	}

	@Override
	public List<String> getClasses() {
		DirectoryScanner classScanner = new DirectoryScanner();
		String baseDir = new PathBuilder().path(targetDir).path("target").path("classes").buildFilePath();
		List<String> ret = new ArrayList<>();

		if (!new File(baseDir).exists()) {
			return ret;
		}
		classScanner.setBasedir(baseDir);
		classScanner.setCaseSensitive(true);
		classScanner.setIncludes(new String[] { "**/*class" });
		classScanner.setExcludes(new String[] { "**/*$*.class" });
		classScanner.scan();

		for (String s : classScanner.getIncludedFiles()) {
			ret.add(s.replaceAll("/", ".").replaceAll("\\\\", ".").replaceAll("\\.class", ""));
		}
		return ret;

	}

	@Override
	public String getClasspath() {
		try {
			if (this.classpath != null)
				return this.classpath;
			final String tacocoCpPath = new PathBuilder().path(this.targetDir).path("tacoco.cp").buildFilePath();

			if (!new File(tacocoCpPath).exists()) {
				System.setProperty("maven.multiModuleProjectDirectory", this.targetDir);
				MavenCli mavenCli = new MavenCli();
				mavenCli.doMain(new String[] { "dependency:build-classpath", "-Dmdep.outputFile=tacoco.cp" },
						this.targetDir, System.out, System.out);
			}

			final String tacocoDependencies = new String(Files.readAllBytes(Paths.get(this.targetDir, "tacoco.cp")));
			final String targetPath = getClassDir();
			final String targetTestPath = getTestclassDir();

			classpath = new PathBuilder().path(tacocoDependencies).path(targetPath).path(targetTestPath)
					.buildClassPath();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return classpath;
	}

	public String getId() {
		return "";

	}

	private void makeFilter(DirectoryScanner scanner) {
		Xpp3Dom dom = null;

		if (getModel().getBuild() != null) {
			for (Plugin p : getModel().getBuild().getPlugins()) {
				if (p.getKey().equals("org.apache.maven.plugins:maven-surefire-plugin")) {
					dom = (Xpp3Dom) p.getConfiguration();
				}
			}
		}

		Xpp3Dom node = null;
		List<String> includes = new ArrayList<>();
		List<String> excludes = new ArrayList<>();

		if (dom != null) {
			node = dom.getChild("includes");
			if (node != null) {
				for (Xpp3Dom n : node.getChildren("include"))
					includes.add(n.getValue().replaceAll("\\.java", "\\.class"));
			}
			node = dom.getChild("test");
			if (node != null) {
				includes.add(node.getValue().replaceAll("\\.java", "\\.class"));
			}

			node = dom.getChild("excludes");
			if (node != null) {
				for (Xpp3Dom n : node.getChildren("exclude"))
					excludes.add(n.getValue().replaceAll("\\.java", "\\.class"));
			}
		}
		// excludes inner classes
		excludes.add("**/*$*.class");

		if (includes.size() == 0)
			scanner.setIncludes(new String[] { "**/Test*.class", "**/*Test.class", "**/*TestCase.class" });
		else
			scanner.setIncludes(includes.toArray(new String[0]));
		scanner.setExcludes(excludes.toArray(new String[0]));
	}

	private Model getModel() {
		Model model = null;
		try {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(new FileInputStream(new File(targetDir, "pom.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	@Override
	public String getClassDir() {
		return new PathBuilder().path(targetDir).path("target").path("classes").buildFilePath();
	}

	@Override
	public String getTestclassDir() {
		return new PathBuilder().path(targetDir).path("target").path("test-classes").buildFilePath();
	}

	@Override
	public int clean() {
		return invokeMavenGoals(Arrays.asList("clean"));
	}

	@Override
	public int compile(Properties properties) {
		return invokeMavenGoals(Arrays.asList("compile", "test-compile"), properties);
	}

	private int invokeMavenGoals(List<String> goals, Properties properties) {
		try {
			InvocationRequest request = new DefaultInvocationRequest();
			request.setPomFile(new File(this.targetDir + "/pom.xml"));
			request.setGoals(goals);
			request.setProperties(properties);

			Invoker invoker = new DefaultInvoker();
			InvocationResult result = invoker.execute(request);

			return result.getExitCode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 100;
	}
}
