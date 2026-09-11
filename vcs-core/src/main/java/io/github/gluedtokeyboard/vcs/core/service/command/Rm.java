package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * Methods for deleting a file.
 */
public class Rm extends BaseCommand {
    /**
     * Used to convert absolute paths to repo relative paths.
     */
    private final GeneralService generalService;

    /**
     * Used for staging/unstaging files.
     */
    private final StagingAreaService stagingAreaService;

    /**
     * Stores the given interface implementations and service objects.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore < Blob >} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     * @param stagingAreaService a {@link StagingAreaService} object initialized with the same interface implementations
     *                           as this object.
     */
    public Rm(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService,
            StagingAreaService stagingAreaService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.generalService = generalService;
        this.stagingAreaService = stagingAreaService;
    }

    /**
     * Stages the current file for removal and deletes it if it is tracked. If it isn't tracked, it is only removed
     * from the staging area if it is staged.
     * @param ctx the repository context.
     * @param path the path of the file.
     */
    public void rm(VcsContext ctx, String path) {
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        boolean isStaged = ctx.vcs().isStaged(repoRelativePath);
        boolean isTracked = isTracked(ctx, repoRelativePath);
        if (isStaged) {
            stagingAreaService.unstageFile(ctx, path);
        }
        if (isTracked) {
            stagingAreaService.stageDeletion(ctx, path);
        }
        if (!(isStaged || isTracked)) {
            throw new RuntimeException("The given file is neither staged nor tracked.");
        }
    }

    /**
     * Checks if the given file is tracked.
     * @param ctx the repository context.
     * @param repoRelativePath the path of the file relative to the repository root.
     * @return {@code true} if the file is tracked; {@code false} otherwise.
     */
    private boolean isTracked(VcsContext ctx, String repoRelativePath) {
        String parentHash = ctx.vcs().getCurrentBranchHead();
        if (parentHash == null) {
            return false;
        }
        Commit parentCommit = commitStore.load(ctx.repoRoot(), parentHash);
        return parentCommit.contains(repoRelativePath);
    }
}
