package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * Methods for resetting the current branch.
 */
public class Reset extends BaseCommand {
    /**
     * Used to apply commits safely.
     */
    private final WorkingTreeService workingTreeService;

    /**
     * Stores the given interface implementations and a {@link WorkingTreeService} object.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param workingTreeService a {@link WorkingTreeService} object initialized with the same interface implementations
     *                           as this object.
     */
    public Reset(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, WorkingTreeService workingTreeService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.workingTreeService = workingTreeService;
    }

    /**
     * Resets the repository to the given commit.
     * @param ctx the repository context.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     */
    public void reset(VcsContext ctx, String commitId) {
        Commit commit = commitStore.load(ctx.repoRoot(), commitId);
        if (commit == null) {
            throw new RuntimeException("Commit with id " + commitId + " not found");
        }
        workingTreeService.applyCommitSafely(ctx, commit);
        ctx.vcs().setCurrentBranchHead(commit.getSha1());
    }
}
