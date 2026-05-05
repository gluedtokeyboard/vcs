package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Staging area methods.
 */
public final class StagingAreaService extends BaseService {
    /**
     * A marker to indicate that a file is staged for deletion.
     */
    public static final String DELETION_MARKER = "";

    /**
     * Used to convert between absolute paths and repository-relative paths.
     */
    private final GeneralService generalService;

    /**
     * Stores the given interface implementations and a {@link GeneralService} object.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     */
    public StagingAreaService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.generalService = generalService;
    }

    /**
     * Stages the given file.
     * @param ctx the repository context.
     * @param path the path of the file.
     */
    public void stageFile(VcsContext ctx, String path) {
        Blob blob = new Blob(storage, path);
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        ctx.vcs().stageFile(repoRelativePath, blob.getSha1());
        blobStore.store(ctx.repoRoot(), blob);
    }

    /**
     * Unstages the given file.
     * @param ctx the repository context.
     * @param path the path of the file.
     */
    public void unstageFile(VcsContext ctx, String path) {
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        ctx.vcs().unstageFile(repoRelativePath);
    }

    /**
     * Stages the given file for deletion.
     * @param ctx the repository context.
     * @param path the path of the file
     */
    public void stageDeletion(VcsContext ctx, String path) {
        storage.rm(path);
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        ctx.vcs().stageFile(repoRelativePath, DELETION_MARKER);
    }

    /**
     * Returns a map from the absolute paths of the staged files to their hashes.
     * @param ctx the repository context.
     * @return a map from the absolute paths of the staged files to their hashes.
     */
    public Map<String, String> getStagedFilesAbsolutePaths(VcsContext ctx) {
        return ctx.vcs().getStagedFilesClone().entrySet().stream()
                .map(entry -> {
                    String repoRelativePath = entry.getKey();
                    String hash = entry.getValue();
                    String absolutePath = generalService.getAbsolutePath(ctx.repoRoot(), repoRelativePath);
                    return new SimpleEntry<>(absolutePath, hash);
                })
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }
}
