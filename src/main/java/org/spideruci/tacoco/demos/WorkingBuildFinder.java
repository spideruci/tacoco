package org.spideruci.tacoco.demos;

import java.util.ArrayList;

import org.spideruci.tacoco.module.IModule;
import org.spideruci.tacoco.module.MavenModule;
import org.spideruci.tacoco.scm.GitRepositoryAnalyzer;
import org.spideruci.tacoco.scm.BranchedCommit;

public class WorkingBuildFinder {

	public static void main(String[] args) {
		final String targetDir = args[0];
		IModule module = new MavenModule(targetDir);
		GitRepositoryAnalyzer gitAnalyzer = GitRepositoryAnalyzer.getAnalyzer(targetDir);
		gitAnalyzer.checkoutBranch("master");

		final int cleanExitStatus = module.clean();
		final int compileExitStatus = module.compile();

		System.out.printf("[EXIT STATUSES] mvn clean: %s; mvn test-compile: %s\n", cleanExitStatus, compileExitStatus);

		int cleanCompileExitStatus = cleanExitStatus + compileExitStatus;

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

				cleanCompileExitStatus = module.clean() + module.compile();
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