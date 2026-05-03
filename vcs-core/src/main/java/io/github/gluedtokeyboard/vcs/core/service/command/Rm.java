package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.nio.file.Path;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.joinCWD;

/**
 * Methods for deleting a file.
 */
public class Rm extends BaseCommand {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore < Blob >} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public Rm(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Stages the current file for removal and deletes it if it is tracked. If it isn't tracked, it is only removed
     * from the staging area if it is staged.
     * @param path the path of the file.
     */
    public void rm(String path) {
        Path p = joinCWD(path);
        boolean isStaged = vcs.isStaged(p);
        boolean isTracked = isTracked(p);
        if (isStaged) {
            stagingAreaService.unstageFile(p);
        }
        if (isTracked) {
            stagingAreaService.stageDeletion(p);
        }
        if (!(isStaged || isTracked)) {
            throw new RuntimeException("The given file is neither staged nor tracked.");
        }
    }

    /**
     * Checks if the given file is tracked.
     * @param path the path of the file
     * @return {@code true} if the file is tracked; {@code false} otherwise.
     */
    private boolean isTracked(String path) {
        String parentHash = vcs.getCurrentBranchHead();
        Commit parentCommit = Commit.deserialize(parentHash);
        return parentCommit.contains(path);
    }
}
