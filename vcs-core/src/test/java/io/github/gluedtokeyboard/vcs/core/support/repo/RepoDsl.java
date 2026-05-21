package io.github.gluedtokeyboard.vcs.core.support.repo;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord;
import io.github.gluedtokeyboard.vcs.core.support.Commands;
import io.github.gluedtokeyboard.vcs.core.support.TestContext;

import java.util.ArrayList;

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

    public String getCurrentBranchHead() {
        return repoCtx.vcs().getCurrentBranchHead();
    }

    public String getCurrentBranch() {
        return repoCtx.vcs().getCurrentBranch();
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

    public RepoDsl rm(final String file) {
        commands.rm().rm(repoCtx, file);
        return this;
    }

    public ArrayList<Commit> log() {
        return commands.log().log(repoCtx);
    }

    public RepoDsl createBranch(String branch) {
        commands.branch().createBranch(repoCtx, branch);
        return this;
    }

    public RepoDsl removeBranch(String branch) {
        commands.branch().removeBranch(repoCtx, branch);
        return this;
    }

    public StatusRecord status() {
        return commands.status().status(repoCtx);
    }

    public RepoDsl checkout(final String file) {
        commands.checkout().checkout(repoCtx, file);
        return this;
    }

    public RepoDsl checkout(final String commitPrefix, final String file) {
        commands.checkout().checkout(repoCtx, commitPrefix, file);
        return this;
    }

    public RepoDsl checkoutBranch(final String branch) {
        commands.checkout().checkoutBranch(repoCtx, branch);
        return this;
    }
}
