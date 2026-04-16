package vcs.utils.stateful;

import vcs.commands.Status;
import vcs.core.Blob;
import vcs.core.Commit;
import vcs.core.VCS;
import vcs.utils.stateless.FileSystem;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static vcs.utils.stateful.General.walkRepoDirsPost;
import static vcs.utils.stateless.FileSystem.rm;
import static vcs.utils.stateless.General.setIntersection;

/**
 * File and commit restoration utils.
 */
public final class Restoration extends StatefulUtil {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Restoration(VCS vcs) {
        super(vcs);
    }

    /**
     * Restores the given file from the given commit.
     * @param commit the {@link Commit} object.
     * @param path the path of the file.
     */
    public static void restoreFile(Commit commit, Path path) {
        String sha1 = commit.getFileHash(path);
        Path blobLocation = CommitUtils.getBlobDestinationCommit(sha1);
        Blob blob = Blob.deserialize(blobLocation);
        blob.writeToFile(path);
    }

    /**
     * Restores the commit only if no data in the repository would be lost.
     * @param commit the {@link Commit} to restore.
     */
    public void restoreCommitSafely(Commit commit) {
        checkSafety(commit);
        removeTrackedFiles(commit);
        restoreCommit(commit);
    }

    /**
     * Checks if it is safe to restore the given commit.
     * @param commit the {@link Commit} to restore.
     * @throws RuntimeException if:
     * <ul>
     *     <li>The staging area isn't empty.</li>
     *     <li>There are unstaged modifications present.</li>
     *     <li>There are untracked files present that would be overwritten.</li>
     * </ul>
     */
    private void checkSafety(Commit commit) {
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
     * @param statusRecord a {@link vcs.commands.Status.StatusRecord} object containing the repository status.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUnstagedModifications(Status.StatusRecord statusRecord) {
        return !(statusRecord.unstagedModifications().isEmpty() && statusRecord.unstagedDeletions().isEmpty());
    }

    /**
     * Checks if the repository has untracked files that would be overwritten while checking out the given
     * {@link Commit}.
     * @param statusRecord a {@link vcs.commands.Status.StatusRecord} object containing the repository status.
     * @param commit the {@link Commit} to check out.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUntrackedConflicts(Status.StatusRecord statusRecord, Commit commit) {
        Set<String> commitFiles = commit.getCommitFiles().collect(Collectors.toSet());
        Set<String> untrackedFiles = statusRecord.untrackedFiles();
        return !setIntersection(commitFiles, untrackedFiles).isEmpty();
    }

    /**
     * Removes the files tracked in the given commit from the repository.
     * @param commit a {@link Commit} object.
     */
    private static void removeTrackedFiles(Commit commit) {
        commit.getCommitFiles()
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
