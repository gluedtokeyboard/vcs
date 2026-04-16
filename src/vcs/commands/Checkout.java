package vcs.commands;

import vcs.core.Blob;
import vcs.core.Commit;
import vcs.core.VCS;
import vcs.utils.stateful.CommitUtils;
import vcs.utils.stateless.FileSystem;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static vcs.utils.stateful.General.walkRepoDirsPost;
import static vcs.utils.stateless.FileSystem.*;
import static vcs.utils.stateless.General.setIntersection;
import static vcs.utils.stateless.Serialization.deserialize;

/**
 * Methods for checking out files and branches.
 */
public class Checkout extends Command {
    /**
     * Stores the current repository, and initializes the repository utils object with it.
     *
     * @param vcs the current repository object.
     */
    public Checkout(VCS vcs) {
        super(vcs);
    }

    /**
     * Checks out the given file from the head of the current branch.
     * @param file the path of the file.
     * @throws RuntimeException if the given file isn't found in the head.
     */
    public void checkout(String file) {
        checkout(vcs.getCurrentBranchHead(), file);
    }

    /**
     * Checks out the given file from the given commit.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @param file the path of the file.
     * @throws RuntimeException if a commit with the given commit ID does not exist, or the given file isn't found in
     * the commit.
     */
    public void checkout(String commitId, String file) {
        Commit commit = getCommit(commitId);
        if (commit == null) {
            throw new RuntimeException("Commit with id " + commitId + " not found");
        }
        Path path = joinCWD(file);
        if (!commit.contains(path)) {
            throw new RuntimeException("File " + file + " not found in commit " + commit.getSha1());
        }
        restoreFile(commit, path);
    }

    /**
     * Restores the given file from the given commit.
     * @param commit the {@link Commit} object.
     * @param path the path of the file.
     */
    private void restoreFile(Commit commit, Path path) {
        String sha1 = commit.getFileHash(path);
        Path blobLocation = CommitUtils.getBlobDestinationCommit(sha1);
        Blob blob = Blob.deserialize(blobLocation);
        blob.writeToFile(path);
    }

    /**
     * Returns the commit matching the given commit ID.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @return the commit matching the given commit ID, or {@code null} if the commit isn't found.
     */
    private Commit getCommit(String commitId) {
        Path directory = Commit.getDestinationFolder(commitId);
        Commit[] commit = {null};
        ls(directory).forEach(path -> {
            String basename = FileSystem.basename(path);
            if (basename.startsWith(commitId)) {
                commit[0] = deserialize(path, Commit.class);
            }
        });
        return commit[0];
    }

    /**
     * Checks out the given branch.
     * @param branch the branch name.
     * @throws RuntimeException if the branch does not exist, or if it is the current branch.
     */
    public void checkoutBranch(String branch) {
        if (!vcs.containsBranch(branch)) {
            throw new RuntimeException("Branch " + branch + " does not exist");
        }
        if (vcs.getCurrentBranch().equals(branch)) {
            throw new RuntimeException("Branch " + branch + " is already the current branch");
        }
        String hash = vcs.getBranchHead(branch);
        Commit commit = Commit.deserialize(hash);
        restoreCommitSafely(commit);
        vcs.setCurrentBranch(branch);
    }

    /**
     * Restores the commit only if no data in the repository would be lost.
     * @param commit the {@link Commit} to restore.
     */
    private void restoreCommitSafely(Commit commit) {
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
    private boolean hasUnstagedModifications(Status.StatusRecord statusRecord) {
        return !(statusRecord.unstagedModifications().isEmpty() && statusRecord.unstagedDeletions().isEmpty());
    }

    /**
     * Checks if the repository has untracked files that would be overwritten while checking out the given
     * {@link Commit}.
     * @param statusRecord a {@link vcs.commands.Status.StatusRecord} object containing the repository status.
     * @param commit the {@link Commit} to check out.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private boolean hasUntrackedConflicts(Status.StatusRecord statusRecord, Commit commit) {
        Set<String> commitFiles = commit.getCommitFiles().collect(Collectors.toSet());
        Set<String> untrackedFiles = statusRecord.untrackedFiles();
        return !setIntersection(commitFiles, untrackedFiles).isEmpty();
    }

    /**
     * Removes the files tracked in the given commit from the repository.
     * @param commit a {@link Commit} object.
     */
    private void removeTrackedFiles(Commit commit) {
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
    private void restoreCommit(Commit commit) {
        commit.getCommitFiles()
                .map(Path::of)
                .forEach(path -> restoreFile(commit, path));
    }
}
