package io.github.gluedtokeyboard.vcs.core.support.commit;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.support.TestContext;

/**
 * Commit utilities.
 */
public final class CommitUtils {
    /**
     * Loads the given commit.
     *
     * @param ctx the {@linkplain TestContext}
     * @param commitPrefix a prefix of the commit's hash
     * @return the {@linkplain Commit} matching the given hash.
     */
    public static Commit loadCommit(final TestContext ctx, final String commitPrefix) {
        final String repoRoot = ctx.repoCtx().repoRoot();
        return ctx.infra().commitStore.load(repoRoot, commitPrefix);
    }

    /**
     * Returns the HEAD {@linkplain Commit}.
     *
     * @param ctx the {@linkplain TestContext}
     * @return the HEAD {@linkplain Commit}
     */
    public static Commit head(final TestContext ctx) {
        final String commitHash = ctx.repoCtx().vcs().getCurrentBranchHead();
        return loadCommit(ctx, commitHash);
    }
}
