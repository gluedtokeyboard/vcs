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

    /**
     * Creates a repository initialized at {@code "/"} with the following commits:
     * <pre>
     *     (main)
     *     +----------------------------+
     *     | Message: add c.txt         |
     *     | Files:                     |
     *     |   a.txt - "hello"          |
     *     |   b.txt - "hello"          |
     *     |   c.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   v
     *     +----------------------------+
     *     | Message: add b.txt         |
     *     | Files:                     |
     *     |   a.txt - "hello"          |
     *     |   b.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   v
     *     +----------------------------+
     *     | Message: add a.txt         |
     *     | Files:                     |
     *     |   a.txt - "hello"          |
     *     +----------------------------+
     * </pre>
     *
     * @param ctx the {@linkplain TestContext}
     * @return a repository with multiple commits
     */
    public static RepoDsl repoWithMultipleCommits(TestContext ctx) {
        return repoWithOneCommit(ctx)
                .add("/b.txt", "hello")
                .commit("add b.txt")
                .add("/c.txt", "hello")
                .commit("add c.txt");
    }
}
