package io.github.gluedtokeyboard.vcs.core.support.repo;

import io.github.gluedtokeyboard.vcs.core.support.TestContext;

/**
 * Predefined repository states for tests.
 */
public final class RepoFixtures {
    /**
     * Creates an empty repository initialized at "/".
     *
     * @param ctx the {@linkplain TestContext}
     * @return an empty repository
     */
    public static RepoDsl emptyRepo(TestContext ctx) {
        final String repoRoot = "/";
        return new RepoDsl(ctx).init(repoRoot);
    }

    /**
     * Creates a repository initialized at "/" with a single commit:
     * <pre>
     *     (main)
     *     +----------------------------+
     *     | Message: add a.txt         |
     *     | Files:                     |
     *     |   a.txt - "hello"          |
     *     +----------------------------+
     * </pre>
     *
     * @param ctx the {@linkplain TestContext}
     * @return a repository with a single commit
     */
    public static RepoDsl repoWithOneCommit(TestContext ctx) {
        return emptyRepo(ctx)
                .add("/a.txt", "hello")
                .commit("add a.txt");
    }
}
