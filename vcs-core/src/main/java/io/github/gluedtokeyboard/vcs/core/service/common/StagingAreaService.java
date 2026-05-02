package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.util.FileSystem;

import java.nio.file.Path;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.join;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.rm;

/**
 * Staging area methods.
 */
public final class StagingAreaService extends BaseService {
    /**
     * A marker to indicate that a file is staged for deletion.
     */
    public static final String DELETION_MARKER = "";

    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public StagingAreaService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     */
    public void stageFile(String path) {
        Blob blob = new Blob(path);
        vcs.stageFile(path, blob.getSha1());
        moveBlobToStagingArea(blob);
    }

    /**
     * Serializes the blob in the staging area folder.
     * @param blob the blob to serialize.
     */
    private static void moveBlobToStagingArea(Blob blob) {
        Path destination = getBlobDestinationStagingArea(blob.getSha1());
        blob.serialize(destination);
    }

    /**
     * Unstages the given file.
     * @param path the path of the file.
     */
    public void unstageFile(String path) {
        removeBlobFromStagingArea(path);
        vcs.unstageFile(path);
    }

    /**
     * Removes the given file's blob from the staging area.
     * @param path The path of the file.
     */
    private void removeBlobFromStagingArea(String path) {
        String sha1 = vcs.getStagedSha(path);
        Path location = getBlobDestinationStagingArea(sha1);
        rm(location);
    }

    /**
     * Stages the given file for deletion.
     * @param path the path of the file
     */
    public void stageDeletion(String path) {
        FileSystem.rm(path);
        vcs.stageFile(path, DELETION_MARKER);
    }

    /**
     * Returns the blob's destination in the staging area.
     * @param sha1 The SHA-1 hash of the blob.
     * @return The blob's destination in the staging area.
     */
    private static String getBlobDestinationStagingArea(String sha1) {
        return join(Vcs.getStagingAreaFolder(), sha1);
    }
}
