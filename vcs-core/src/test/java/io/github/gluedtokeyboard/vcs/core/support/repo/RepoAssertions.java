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
}
