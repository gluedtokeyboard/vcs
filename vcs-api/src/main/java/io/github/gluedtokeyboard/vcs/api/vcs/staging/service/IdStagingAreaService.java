package io.github.gluedtokeyboard.vcs.api.vcs.staging.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import org.springframework.stereotype.Service;

/**
 * {@link StagingAreaService} with ID support.
 */
@Service
public class IdStagingAreaService extends StagingAreaService {
    private final StagingService stagingService;

    /**
     * Stores the given interface implementations and a {@link GeneralService} object.
     *
     * @param storage        an {@link IdStorage}
     * @param vcsLayout      a {@link VcsLayout} implementation
     * @param vcsStore       a {@link VcsStore} implementation
     * @param blobStore      a {@link ContentAddressableStore<Blob>} implementation
     * @param commitStore    a {@link ContentAddressableStore<Commit>} implementation
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object
     * @param stagingService the object containing
     *                       {@link io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry} service methods
     */
    public IdStagingAreaService(
            IdStorage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService, StagingService stagingService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore, generalService);
        this.stagingService = stagingService;
    }

    @Override
    public void stageFile(VcsContext ctx, String path) {
        super.stageFile(ctx, path);
        stagingService.createAdditionEntry(ctx.repoRoot(), path);
    }

    @Override
    public void unstageFile(VcsContext ctx, String path) {
        super.unstageFile(ctx, path);
        stagingService.deleteEntry(ctx.repoRoot(), path);
    }

    @Override
    public void stageDeletion(VcsContext ctx, String path) {
        super.stageDeletion(ctx, path);
        stagingService.createDeletionEntry(ctx.repoRoot(), path);
    }

    @Override
    public void clearStagingArea(VcsContext ctx) {
        super.clearStagingArea(ctx);
        stagingService.deleteEntries(ctx.repoRoot());
    }
}
