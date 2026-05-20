package io.github.gluedtokeyboard.vcs.core.support.repo;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.support.Commands;
import io.github.gluedtokeyboard.vcs.core.support.TestContext;

/**
 * A DSL for repository operations.
 */
public final class RepoDsl {
    private final TestContext ctx;
    private final Commands commands;
    private VcsContext repoCtx;

    public RepoDsl(final TestContext ctx) {
        this.ctx = ctx;
        commands = ctx.commands();
    }

    VcsContext repoCtx() {
        return repoCtx;
    }

    public String repoRoot() {
        return repoCtx.repoRoot();
    }

    public RepoDsl init(final String repoRoot) {
        commands.init().init(repoRoot);
        ctx.setRepoCtx(repoRoot);
        repoCtx = ctx.repoCtx();
        return this;
    }

    public RepoDsl add(final String file) {
        commands.add().add(repoCtx, file);
        return this;
    }

    public RepoDsl add(final String file, final String contents) {
        ctx.storage().write(contents, file);
        return add(file);
    }

    public RepoDsl commit(final String message) {
        commands.commitCommand().commit(repoCtx, message);
        return this;
    }
}
