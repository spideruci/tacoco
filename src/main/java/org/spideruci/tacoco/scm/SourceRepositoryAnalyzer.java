package org.spideruci.tacoco.scm;

import java.util.List;
import org.spideruci.tacoco.scm.BranchedCommit;

public interface SourceRepositoryAnalyzer {
	public abstract Iterable<String> getLatestCommitIds(int num);
	public abstract String getCommitMessage(String commitId);
	public abstract String getCommitAuthor(String commitId);
	public abstract BranchedCommit checkoutCommit(String commitId);
	public abstract BranchedCommit checkoutBranch(String branchName);
	public abstract List<String> deleteBranch(String branchName);
}