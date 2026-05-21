package io.github.gluedtokeyboard.vcs.core.support.repo;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Assertions for repository state.
 */
public final class RepoAssertions {
    private final RepoDsl repo;

    private RepoAssertions(final RepoDsl repo) {
        this.repo = repo;
    }

    public static RepoAssertions assertThat(final RepoDsl repo) {
        return new RepoAssertions(repo);
    }

    public RepoAssertions isStaged(final String repoRelativePath) {
        final boolean isStaged = repo.repoCtx().vcs().isStaged(repoRelativePath);
        final String message = "Expected " + repoRelativePath + " to be staged";
        assertTrue(isStaged, message);
        return this;
    }

    public RepoAssertions isNotStaged(final String repoRelativePath) {
        final boolean isStaged = repo.repoCtx().vcs().isStaged(repoRelativePath);
        final String message = "Expected " + repoRelativePath + " to not be staged";
        assertFalse(isStaged, message);
        return this;
    }

    public RepoAssertions containsBranch(final String branch) {
        final boolean containsBranch = repo.repoCtx().vcs().containsBranch(branch);
        final String message = "Expected repo to contain branch " + branch;
        assertTrue(containsBranch, message);
        return this;
    }

    public RepoAssertions doesNotContainBranch(final String branch) {
        final boolean containsBranch = repo.repoCtx().vcs().containsBranch(branch);
        final String message = "Expected repo to not contain branch " + branch;
        assertFalse(containsBranch, message);
        return this;
    }

    public RepoAssertions currentBranchEquals(final String expectedBranch) {
        final String currentBranch = repo.repoCtx().vcs().getCurrentBranch();
        final String message = "Expected the current branch to be: " + expectedBranch + " but got: " + currentBranch;
        assertEquals(expectedBranch, currentBranch, message);
        return this;
    }

    public RepoAssertions currentBranchHeadEquals(final String expectedCommitHash) {
        final String currentBranchHead = repo.getCurrentBranchHead();
        final String message =
                "Expected the current branch head to be: "
                + expectedCommitHash
                + " but got: "
                + currentBranchHead;
        assertEquals(expectedCommitHash, currentBranchHead, message);
        return this;
    }
}
