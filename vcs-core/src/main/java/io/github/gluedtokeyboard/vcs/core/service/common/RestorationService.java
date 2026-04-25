package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.util.FileSystem;

import java.nio.file.Path;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommittedBlob;
import static io.github.gluedtokeyboard.vcs.core.service.common.GeneralService.walkRepoDirsPost;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.rm;

/**
 * File and commit restoration methods.
 */
public final class RestorationService extends BaseService {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public RestorationService(Vcs vcs) {
        super(vcs);
    }

    /**
     * Restores the given file from the given commit.
     * @param commit the {@link Commit} object.
     * @param path the path of the file.
     */
    public static void restoreFile(Commit commit, Path path) {
        String sha1 = commit.getFileHash(path);
        Blob blob = getCommittedBlob(sha1);
        blob.writeToFile(path);
    }

    /**
     * Restores the commit only if no data in the repository would be lost.
     * @param commit the {@link Commit} to restore.
     */
    public void restoreCommitSafely(Commit commit) {
        new CommitService(vcs).checkSafety(commit);
        removeTrackedFiles();
        restoreCommit(commit);
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
     * Restores the given {@link Commit}.
     * @param commit the {@link Commit} to restore.
     */
    private static void restoreCommit(Commit commit) {
        commit.getCommitFiles()
                .map(Path::of)
                .forEach(path -> restoreFile(commit, path));
    }
}
