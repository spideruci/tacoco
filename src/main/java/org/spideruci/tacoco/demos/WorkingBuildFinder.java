package org.spideruci.tacoco.demos;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.spideruci.tacoco.module.GradleModule;
import org.spideruci.tacoco.module.IModule;
import org.spideruci.tacoco.module.MavenModule;
import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.spideruci.tacoco.scm.GitRepositoryAnalyzer;
import org.spideruci.tacoco.scm.BranchedCommit;

public class WorkingBuildFinder {

	public static IModule detectBuilder(final String absoluteTargetPath) {
		if (new File(absoluteTargetPath, "pom.xml").exists()) {
			return new MavenModule(absoluteTargetPath);
		}
		if (new File(absoluteTargetPath, "build.gradle").exists()) {
			return new GradleModule(absoluteTargetPath);
		}
		return null;
	}

	public static void main(String[] args) throws ParseException {
		Options options = new Options();

		options.addOption("s", "sut", true, "Path to the system under study.");
		options.addOption("c", "commit", true, "Starting commit");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		final String targetDir;
		final String commit_sha;

		if (cmd.hasOption("sut")) {
			targetDir = cmd.getOptionValue("sut");
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("CLITester", options);
			System.exit(1);
			return;
		}

		if (cmd.hasOption("commit")) {
			commit_sha = cmd.getOptionValue("commit");
		} else {
			commit_sha = null;
		}

		IModule module = detectBuilder(targetDir);
		if (module == null) {
			System.out.println("[Error] build system not detected...");
			System.exit(1);
		}

		GitRepositoryAnalyzer gitAnalyzer = GitRepositoryAnalyzer.getAnalyzer(targetDir);
		gitAnalyzer.checkoutBranch("master");

		if (commit_sha != null) {
			gitAnalyzer.checkoutCommit(commit_sha);
		}

		Properties properties = new Properties();
		properties.put("maven.compiler.source", "1.8");
		properties.put("maven.compiler.target", "1.8");

		final int cleanExitStatus = module.clean();
		final int compileExitStatus = module.compile(properties);

		System.out.printf("[EXIT STATUSES] mvn clean: %s; mvn test-compile: %s\n", cleanExitStatus, compileExitStatus);

		int cleanCompileExitStatus = compileExitStatus;

		if (cleanCompileExitStatus == 0) {
			String commitId = gitAnalyzer.getLatestCommitIds(1).iterator().next();
			System.out.printf("[FOUND IT!] %s, %s\n", targetDir, commitId);

		} else {
			Iterable<String> commitIds = gitAnalyzer.getLatestCommitIds(10);
			ArrayList<BranchedCommit> branchedCommits = new ArrayList<>();

			for (String commitId : commitIds) {
				BranchedCommit branchedCommit = gitAnalyzer.checkoutCommit(commitId);
				if (branchedCommit == null) {
					continue;
				}

				branchedCommits.add(branchedCommit);

				System.out.printf("[TRYING NEXT COMMIT] %s \n", commitId);

				cleanCompileExitStatus = module.compile(properties);
				if (cleanCompileExitStatus == 0) {
					System.out.printf("[FOUND IT!] %s, %s\n", targetDir, commitId);
					break;
				}
			}

			gitAnalyzer.checkoutBranch("master");

			for (BranchedCommit commit : branchedCommits) {
				gitAnalyzer.deleteBranch(commit.branchName);
			}
		}

		gitAnalyzer.checkoutBranch("master");
	}
}