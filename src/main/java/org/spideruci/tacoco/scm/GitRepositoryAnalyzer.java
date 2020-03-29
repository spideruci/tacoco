package org.spideruci.tacoco.scm;

import org.spideruci.tacoco.scm.SourceRepositoryAnalyzer;
import org.spideruci.tacoco.scm.BranchedCommit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.MissingObjectException;

import org.apache.log4j.Logger;
import org.apache.log4j.Category;
import org.apache.log4j.Level;

public class GitRepositoryAnalyzer implements SourceRepositoryAnalyzer {

	private final File gitRepoPath;
	private final Git repository;

	static public GitRepositoryAnalyzer getAnalyzer(String gitRepoFullPath) {
		try {
			File file = new File(gitRepoFullPath);
			if (file.exists() && file.isDirectory() && file.isAbsolute()) {
				Git repository = Git.open(file);
				GitRepositoryAnalyzer repoProbe = new GitRepositoryAnalyzer(file, repository);
				return repoProbe;
			}
		} catch (NullPointerException | IOException e) {
			return null;
		}

		return null;
	}

	private GitRepositoryAnalyzer(File repoPath, Git repo) {
		this.gitRepoPath = repoPath;
		this.repository = repo;
	}

	/*
	 * Calls Git Log
	 */
	public Iterable<String> getLatestCommitIds(int num) {
		ArrayList<String> commitIds = new ArrayList<>();

		try {
			Iterable<RevCommit> commits = repository.log().setMaxCount(num).call();
			for (RevCommit commit : commits) {
				String commidId = ObjectId.toString(commit.getId());
				commitIds.add(commidId);
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		return commitIds;
	}

	private RevCommit getCommit(String shaStringRepr) {
		RevWalk revWalk = new RevWalk(repository.getRepository());
		ObjectId objectId = ObjectId.fromString(shaStringRepr);
		try {
			RevCommit commit = revWalk.lookupCommit(objectId);
			revWalk.parseBody(commit);
			return commit;
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getCommitMessage(String commitId) {
		RevCommit commit = this.getCommit(commitId);
		return commit == null ? "" : commit.getShortMessage();
	}

	public String getCommitAuthor(String commitId) {
		RevCommit commit = this.getCommit(commitId);
		return commit == null ? "" : commit.getAuthorIdent().getName();
	}

	protected String commitIdForBranch(String branchName) {
		try {
			String commitdId = null;
			ListBranchCommand listBranch = repository.branchList();
			List<Ref> refs = listBranch.setListMode(ListBranchCommand.ListMode.ALL).call();
			for (Ref ref : refs) {
				if (ref == null)
					continue;

				System.err.println("[DEBUG] ref: " + ref.getName());

				if (branchName.equals(ref.getName())) {
					commitdId = ObjectId.toString(ref.getObjectId());
					break;
				}
			}

			return commitdId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> deleteBranch(String branchName) {
		try {
			DeleteBranchCommand deleteBranch = repository.branchDelete();
			List<String> deletedBranchNames = deleteBranch.setBranchNames(branchName).call();
			for (String deletedBranchName : deletedBranchNames) {
				System.out.printf("[DEBUG] Deleted branch name: %s \n", deletedBranchName);
			}

			return deletedBranchNames;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public BranchedCommit checkoutCommit(String commitId) {
		if (this.commitIdForBranch("refs/heads/" + Constants.MASTER).equals(commitId)) {
			// If MASTER is already checked out, then bail early.
			return null;
		}

		if (this.checkoutBranch(Constants.MASTER) == null) {
			// When checking out to an arbitary commit, it is best to reset
			// HEAD to MASTER. This will make sure that we are not trying to
			// checking out something that is already checked out.
			return null;
		}

		try {
			CheckoutCommand checkout = repository.checkout();
			RevCommit commit = this.getCommit(commitId);
			if (commit != null) {
				final String commitName = commit.name();
				final String newBranchName = "tacoco-b-" + commitName;

				this.deleteBranch(newBranchName);

				Ref ref = checkout.setCreateBranch(true).setName(newBranchName).setStartPoint(commit).call();

				System.out.println("[DEBUG] Is ref null? " + (ref == null));
				System.out.println("[DEBUG] Checked out ref-name: " + ref.getName());
				final String refObjectIdName = ObjectId.toString(ref.getObjectId());

				return new BranchedCommit(newBranchName, refObjectIdName);
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		return null;
	}

	public BranchedCommit checkoutBranch(String branchName) {
		try {
			CheckoutCommand checkout = repository.checkout();
			Ref ref = checkout.setName(branchName).call();
			String commitId = ObjectId.toString(ref.getObjectId());
			return new BranchedCommit(branchName, commitId);
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		return null;
	}

	static {
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	public static void main(String[] args) {
		final String gitRepoDir = args[0];
		System.out.printf("GitProbe: analyzing %s\n", gitRepoDir);

		GitRepositoryAnalyzer gitProbe = GitRepositoryAnalyzer.getAnalyzer(gitRepoDir);

		for (String commitId : gitProbe.getLatestCommitIds(10)) {
			String commitMsg = gitProbe.getCommitMessage(commitId);
			String commitAuthor = gitProbe.getCommitAuthor(commitId);
			System.out.printf("%s: %s [%s]\n", commitId, commitMsg, commitAuthor);
		}

		Iterator<String> commitIds = gitProbe.getLatestCommitIds(2).iterator();

		ArrayList<BranchedCommit> branchedCommits = new ArrayList<>();

		while (commitIds.hasNext()) {
			String commitId = commitIds.next();
			BranchedCommit branchedCommit = gitProbe.checkoutCommit(commitId);

			if (branchedCommit == null) {
				System.out.printf("[WARNING] %s already checked out. Skipping check out.\n", commitId);
				continue;
			}

			branchedCommits.add(branchedCommit);
			System.out.println(branchedCommit.toString());
		}

		BranchedCommit masterBranch = gitProbe.checkoutBranch(Constants.MASTER);
		System.out.println(masterBranch.toString());

		for (BranchedCommit branchedCommit : branchedCommits) {
			if (branchedCommit == null) {
				continue;
			}

			System.out.printf("Deleting %s\n", branchedCommit.branchName);
			gitProbe.deleteBranch(branchedCommit.branchName);
		}
	}
}
