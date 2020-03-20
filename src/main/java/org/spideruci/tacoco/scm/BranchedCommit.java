package org.spideruci.tacoco.scm;

public class BranchedCommit {
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