package io.github.gluedtokeyboard.vcs.api.vcs.staging.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoService;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.model.EntryType;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

/**
 * {@link StagingAreaService} with ID support.
 */
@Service
public class IdStagingAreaService extends StagingAreaService {
    private final StagingService stagingService;
    private final RepoService repoService;

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
     * @param stagingService the object containing {@link StagingEntry} service methods
     * @param repoService the object containing {@link Repo} service methods
     */
    public IdStagingAreaService(
            IdStorage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService, StagingService stagingService,
            RepoService repoService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore, generalService);
        this.stagingService = stagingService;
        this.repoService = repoService;
    }

    @Override
    public void stageFile(VcsContext ctx, String path) {
        stage(ctx, path, EntryType.ADDITION);
    }

    @Override
    public void unstageFile(VcsContext ctx, String path) {
        super.unstageFile(ctx, path);
        stagingService.deleteEntry(ctx.repoRoot(), path);
    }

    @Override
    public void stageDeletion(VcsContext ctx, String path) {
        stage(ctx, path, EntryType.DELETION);
    }

    @Override
    public void clearStagingArea(VcsContext ctx) {
        super.clearStagingArea(ctx);
        stagingService.deleteEntries(ctx.repoRoot());
    }

    /**
     * Stages the given file for addition or deletion.
     *
     * @param ctx the repository context
     * @param path the path of the file
     * @param entryType the staging entry type
     */
    private void stage(VcsContext ctx, String path, EntryType entryType) {
        BiConsumer<VcsContext, String> stagingFunction = switch (entryType) {
            case EntryType.ADDITION -> super::stageFile;
            case EntryType.DELETION -> super::stageDeletion;
        };
        stagingFunction.accept(ctx, path);
        String repoRoot = ctx.repoRoot();
        Repo repo = repoService.getRepo(repoRoot);
        String relativePath = generalService.getRepoRelativePath(repoRoot, path);
        try {
            StagingEntry entry = stagingService.getEntry(repo.getId(), relativePath);
            if (entry.getType() != entryType) {
                entry.setType(entryType);
            }
        } catch (NoSuchElementException e) {
            BiConsumer<String, String> entryCreationFunction = switch (entryType) {
                case EntryType.ADDITION -> stagingService::createAdditionEntry;
                case EntryType.DELETION -> stagingService::createDeletionEntry;
            };
            entryCreationFunction.accept(repoRoot, path);
        }
    }
}
