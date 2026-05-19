package io.github.gluedtokeyboard.vcs.core.support;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.support.infra.VcsInfra;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * Holds the VCS infrastructure, the {@linkplain Commands}, and the repository context.
 */
public final class TestContext {
    private final VcsInfra vcsInfra;
    private final Storage storage;
    private final Commands commands;
    private VcsContext repoCtx;

    public TestContext() {
        vcsInfra = new VcsInfra();
        storage = vcsInfra.storage;
        commands = new Commands(vcsInfra);
        repoCtx = null;
    }

    public VcsInfra infra() {
        return vcsInfra;
    }

    public Storage storage() {
        return storage;
    }

    public Commands commands() {
        return commands;
    }

    public VcsContext repoCtx() {
        return repoCtx;
    }

    public void setRepoCtx(String repoRoot) {
        repoCtx = VcsContext.getContext(storage, infra().vcsStore, repoRoot);
    }
}
