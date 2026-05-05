package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.Objects;

/**
 * Methods for staging a file.
 */
public class Add extends BaseCommand {
    /**
     * Used to convert absolute paths to repo relative paths.
     */
    private final GeneralService generalService;

    /**
     * Used for staging/unstaging files.
     */
    private final StagingAreaService stagingAreaService;

    /**
     * Used to check whether the given file has changed since the previous commit.
     */
    private final CommitService commitService;

    /**
     * Stores the given interface implementations and service objects.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     * @param stagingAreaService a {@link StagingAreaService} object initialized with the same interface implementations
     *                           as this object.
     * @param commitService a {@link CommitService} object initialized with the same interface implementations as this
     *                       object.
     */
    public Add(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService,
            StagingAreaService stagingAreaService, CommitService commitService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.generalService = generalService;
        this.stagingAreaService = stagingAreaService;
        this.commitService = commitService;
    }

    /**
     * Stages the given file.
     * @param ctx the repository context.
     * @param path the path of the file.
     */
    public void add(VcsContext ctx, String path) {
        if (!storage.exists(path)) {
            throw new RuntimeException("File does not exist");
        }
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        if (ctx.vcs().isStaged(repoRelativePath)) {
            stagingAreaService.unstageFile(ctx, path);
        }
        if (hasChanged(ctx, path)) {
            stagingAreaService.stageFile(ctx, path);
        }
    }

    /**
     * Checks if the given file has changed compared to the previous commit.
     * @param ctx the repository context.
     * @param path the path of the file.
     * @return {@code true} if the file has changed; {@code false} otherwise.
     */
    private boolean hasChanged(VcsContext ctx, String path) {
        Commit commit = commitService.getLatestCommitCurrentBranch(ctx);
        if (commit == null) {
            return true;
        }
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        String previousHash = commit.getFileHash(repoRelativePath);
        Blob blob = new Blob(storage, path);
        return !Objects.equals(blob.getSha1(), previousHash);
    }
}
