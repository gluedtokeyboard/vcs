package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.util.FileSystem;

import java.nio.file.Path;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommittedBlob;
import static io.github.gluedtokeyboard.vcs.core.service.common.GeneralService.walkRepoDirsPost;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.rm;

/**
 * Methods to restore files, and safely apply commits.
 */
public final class RestorationService extends BaseService {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public RestorationService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Restores the given file from the given commit.
     * @param commit the {@link Commit} object.
     * @param path the path of the file.
     */
    public static void restoreFile(Commit commit, String path) {
        String sha1 = commit.getFileHash(path);
        Blob blob = getCommittedBlob(sha1);
        blob.writeToFile(path);
    }

    /**
     * Applies the commit only if no data in the repository would be lost.
     * @param commit the {@link Commit} to apply.
     */
    public void applyCommitSafely(Commit commit) {
        new CommitService(vcs).checkSafety(commit);
        removeTrackedFiles();
        applyCommit(commit);
    }

    /**
     * Removes the files tracked in the head commit.
     */
    private void removeTrackedFiles() {
        Commit head = Commit.deserialize(vcs.getCurrentBranchHead());
        head.getCommitFiles()
                .map(Path::of)
                .forEach(FileSystem::rm);
        removeEmptyDirectories();
    }

    /**
     * Removes empty directories in the repository.
     */
    private static void removeEmptyDirectories() {
        walkRepoDirsPost(path -> {
            try {
                rm(path);
            } catch (RuntimeException e) {
                /* Directory not empty, so do nothing. */
            }
        });
    }

    /**
     * Applies the given {@link Commit}.
     * @param commit the {@link Commit} to apply.
     */
    private static void applyCommit(Commit commit) {
        commit.getCommitFiles()
                .map(Path::of)
                .forEach(path -> restoreFile(commit, path));
    }
}
