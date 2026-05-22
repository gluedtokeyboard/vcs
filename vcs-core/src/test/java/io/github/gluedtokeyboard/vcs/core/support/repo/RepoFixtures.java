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

    /**
     * Creates a repository initialized at {@code "/"} with the following commits and branches:
     * <pre>
     *     (main)
     *     +----------------------------+
     *     | Message: add d.txt         |
     *     | Files:                     |
     *     |   b.txt - "hello world"    |
     *     |   c.txt - "hello"          |
     *     |   d.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   v
     *     +----------------------------+
     *     | Message: update b.txt      |
     *     | Files:                     |
     *     |   b.txt - "hello world"    |
     *     |   c.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   v
     *     +----------------------------+
     *     | Message: remove a.txt      |
     *     | Files:                     |
     *     |   b.txt - "hello"          |
     *     |   c.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   v
     *     (development)
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
     * @return a repository with multiple branches
     */
    public static RepoDsl repoWithMultipleBranches(TestContext ctx) {
        return repoWithMultipleCommits(ctx)
                .createBranch("development")
                .rm("/a.txt")
                .commit("remove a.txt")
                .add("/b.txt", "hello world")
                .commit("update b.txt")
                .add("/d.txt", "hello")
                .commit("add d.txt");
    }

    /**
     * Creates a repository initialized at {@code "/"} with the following commits and branches:
     * <pre>
     *     (main)
     *     +----------------------------+
     *     | Message: add d.txt         |
     *     | Files:                     |
     *     |   b.txt - "hello world"    |
     *     |   c.txt - "hello"          |
     *     |   d.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   v
     *     +----------------------------+
     *     | Message: update b.txt      |
     *     | Files:                     |
     *     |   b.txt - "hello world"    |
     *     |   c.txt - "hello"          |
     *     +----------------------------+
     *                   |
     *                   |
     *                   v                        (development)
     *     +----------------------------+         +----------------------------+
     *     | Message: remove a.txt      |         | Message: add e.txt         |
     *     | Files:                     |         | Files:                     |
     *     |   b.txt - "hello"          |         |   a.txt - "hello"          |
     *     |   c.txt - "hello"          |         |   b.txt - "hello"          |
     *     |                            |         |   c.txt - "hello"          |
     *     |                            |         |   e.txt - "hello"          |
     *     +----------------------------+         +----------------------------+
     *                   |                                      |
     *                   v                                      |
     *     +----------------------------+                       |
     *     | Message: add c.txt         |                       |
     *     | Files:                     |                       |
     *     |   a.txt - "hello"          |<----------------------|
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
     * @return a repository with non-linear history
     */
    public static RepoDsl repoWithNonLinearHistory(TestContext ctx) {
        return repoWithMultipleBranches(ctx)
                .checkoutBranch("development")
                .add("/e.txt", "hello")
                .commit("add e.txt")
                .checkoutBranch("main");
    }
}
