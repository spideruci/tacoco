package org.spideruci.tacoco.scm;

import java.util.List;

public interface SourceRepositoryAnalyzer {
    public abstract Iterable<String> getLatestCommitIds(int num);
    public abstract String getCommitMessage(String commitId);
    public abstract String getCommitAuthor(String commitId);
    public abstract BranchedCommit checkoutCommit(String commitId);
    public abstract BranchedCommit checkoutBranch(String branchName);
    public abstract List<String> deleteBranch(String branchName);
}

class BranchedCommit {
    public final String branchName;
    public final String commitId;

    public BranchedCommit(final String branchName, final String commitId) {
        this.branchName = branchName;
        this.commitId = commitId;
    }

    @Override public String toString() {
        return String.format("%s//%s", this.branchName, this.commitId);
    }
}