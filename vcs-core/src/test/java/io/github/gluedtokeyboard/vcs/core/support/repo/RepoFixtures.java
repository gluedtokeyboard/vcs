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
}
