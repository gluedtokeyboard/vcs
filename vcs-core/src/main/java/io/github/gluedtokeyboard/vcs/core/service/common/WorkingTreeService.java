package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.Status;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.util.FileSystem;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommittedBlob;
import static io.github.gluedtokeyboard.vcs.core.service.common.GeneralService.walkRepoDirsPost;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.rm;
import static io.github.gluedtokeyboard.vcs.util.General.setIntersection;

/**
 * Methods to restore files, and safely apply commits.
 */
public final class WorkingTreeService extends BaseService {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public WorkingTreeService(
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
        checkSafety(commit);
        removeTrackedFiles();
        applyCommit(commit);
    }

    /**
     * Checks if it is safe to apply the given commit.
     * @param commit the {@link Commit} to apply.
     * @throws RuntimeException if:
     * <ul>
     *     <li>The staging area isn't empty.</li>
     *     <li>There are unstaged modifications present.</li>
     *     <li>There are untracked files present that would be overwritten.</li>
     * </ul>
     */
    public void checkSafety(Commit commit) {
        if (!vcs.stagingAreaIsEmpty()) {
            throw new RuntimeException("The staging area isn't empty");
        }
        Status.StatusRecord statusRecord = new Status(vcs).status();
        if (hasUnstagedModifications(statusRecord)) {
            throw new RuntimeException("There are unstaged modifications present");
        }
        if (hasUntrackedConflicts(statusRecord, commit)) {
            throw new RuntimeException("There are untracked files present that would be overwritten");
        }
    }

    /**
     * Checks if the repository has unstaged modifications.
     * @param statusRecord a {@link io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord} object containing the repository status.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUnstagedModifications(Status.StatusRecord statusRecord) {
        return !(statusRecord.unstagedModifications().isEmpty() && statusRecord.unstagedDeletions().isEmpty());
    }

    /**
     * Checks if the repository has untracked files that would be overwritten while checking out the given
     * {@link Commit}.
     * @param statusRecord a {@link io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord} object containing the repository status.
     * @param commit the {@link Commit} to check out.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUntrackedConflicts(Status.StatusRecord statusRecord, Commit commit) {
        Set<String> commitFiles = commit.getCommitFiles().collect(Collectors.toSet());
        Set<String> untrackedFiles = statusRecord.untrackedFiles();
        return !setIntersection(commitFiles, untrackedFiles).isEmpty();
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
