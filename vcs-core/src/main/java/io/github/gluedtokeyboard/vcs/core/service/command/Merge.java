package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.VCS;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommitsInBFSOrder;
import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommittedBlob;
import static io.github.gluedtokeyboard.vcs.core.service.common.RestorationService.restoreFile;
import static io.github.gluedtokeyboard.vcs.util.File.write;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.basename;
import static io.github.gluedtokeyboard.vcs.util.General.*;

/**
 * Methods to merge branches.
 */
public class Merge extends BaseCommand {
    /**
     * Stores the current repository, and initializes the repository utils object with it.
     *
     * @param vcs the current repository object.
     */
    public Merge(VCS vcs) {
        super(vcs);
    }

    /**
     * Merges the given branch into the current branch.
     * @param branchName the name of the branch.
     */
    public void merge(String branchName) {
        if (branchName.equals(vcs.getCurrentBranch())) {
            throw new RuntimeException("You are already on branch " + branchName + ".");
        }
        if (!vcs.containsBranch(branchName)) {
            throw new RuntimeException("Branch " + branchName + " does not exist.");
        }
        String otherSha1 = vcs.getBranchHead(branchName);
        Commit other = Commit.deserialize(otherSha1);
        String currentSha1 = vcs.getCurrentBranchHead();
        Commit current = Commit.deserialize(currentSha1);
        Commit splitPoint = getSplitPoint(current, other);
        checkMergeNeeded(current, other, splitPoint, branchName);
        commitService.checkSafety(other);
        mergeCommits(current, other, splitPoint, branchName);
    }

    /**
     * Returns the split point between the two commits.
     * @param commit1 the first {@link Commit}.
     * @param commit2 the second {@link Commit}.
     * @return the split point between the two commits.
     */
    private Commit getSplitPoint(Commit commit1, Commit commit2) {
        Set<String> commit1Ancestors = new HashSet<>();
        getCommitsInBFSOrder(commit1).forEach(commit1Ancestors::add);
        Iterable<String> commit2Ancestors = getCommitsInBFSOrder(commit2);
        for (String commit2Ancestor : commit2Ancestors) {
            if (commit1Ancestors.contains(commit2Ancestor)) {
                return Commit.deserialize(commit2Ancestor);
            }
        }
        throw new RuntimeException(
                "Shouldn't be here. The split point between two commits is guaranteed to exist as all commits have the "
                + "initial commit as a common ancestor."
        );
    }

    /**
     * Checks if a merge is needed between the two commits.
     * @param current the current {@link Commit}.
     * @param other the {@link Commit} to merge into the current {@link Commit}.
     * @param splitPoint the split point between the two commits.
     * @param branchName the name of the branch to merge into the current branch.
     * @throws RuntimeException if:
     * <ul>
     *     <li>The current branch can be fast-forwarded. In this case the branch is fast-forwarded before the
     *     exception is thrown.</li>
     *     <li>The given branch in an ancestor of the current branch.</li>
     * </ul>
     */
    private void checkMergeNeeded(Commit current, Commit other, Commit splitPoint, String branchName) {
        String splitPointSha1 = splitPoint.getSha1();
        String currentSha1 = current.getSha1();
        String otherSha1 = other.getSha1();
        if (splitPointSha1.equals(currentSha1)) {
            new Checkout(vcs).checkoutBranch(branchName);
            vcs.setCurrentBranchHead(otherSha1);
            throw new RuntimeException("Current branch fast-forwarded.");
        } else if (splitPointSha1.equals(otherSha1)) {
            throw new RuntimeException("The given branch is an ancestor of the current branch.");
        }
    }

    /**
     * Merges the two commits.
     * @param current the current branch head.
     * @param other the branch head of {@code branchName}.
     * @param splitPoint the split point between the two commits.
     * @param branchName the name of the branch to merge into the current branch.
     */
    private void mergeCommits(Commit current, Commit other, Commit splitPoint, String branchName) {
        // Handle modifications.
        Set<String> currentModifications = getModifications(splitPoint, current);
        Set<String> otherModifications = getModifications(splitPoint, other);
        Set<String> otherExclusiveModifications = setDifference(otherModifications, currentModifications);
        restoreAndStageCommitFiles(other, otherExclusiveModifications);
        Set<String> modificationConflicts = setIntersection(currentModifications, otherModifications);
        // Handle deletions.
        Set<String> currentDeletions = getDeletions(splitPoint, current);
        Set<String> otherDeletions = getDeletions(splitPoint, other);
        Set<String> toBeDeleted = // Files deleted in the other commit but unmodified in the current commit.
                setDifference(otherDeletions, currentDeletions, currentModifications);
        removeAndStageFiles(toBeDeleted);
        Set<String> deletionConflicts = // Files that are deleted in one commit but modified in the other.
                setUnion(
                        setIntersection(currentDeletions, otherModifications),
                        setIntersection(otherDeletions, currentModifications)
                );
        // Handle merge conflicts if any, and make a merge commit.
        Set<String> conflicts = setUnion(modificationConflicts, deletionConflicts);
        if (!conflicts.isEmpty()) {
            mergeConflicts(current, other, conflicts, branchName);
        }
        createMergeCommit(branchName);
        if (!conflicts.isEmpty()) {
            String conflictMessage = getConflictMessage(conflicts);
            throw new RuntimeException(conflictMessage);
        }
    }

    /**
     * Creates a merge commit.
     * @param branchName the name of the branch to merge into the current branch.
     */
    private void createMergeCommit(String branchName) {
        String branchHead = vcs.getBranchHead(branchName);
        String commitMessage = getMergeCommitMessage(branchName);
        new CommitCommand(vcs).commit(branchHead, commitMessage);
    }

    /**
     * Returns the modifications made in {@code commit} relative to {@code base}.
     * @param base the base {@link Commit}.
     * @param commit the {@link Commit} with the modifications.
     * @return a {@link Set} containing the modifications.
     */
    private Set<String> getModifications(Commit base, Commit commit) {
        Map<String, String> modifications = commit.getFilesClone();
        Map<String, String> baseFiles = base.getFilesClone();
        baseFiles.forEach((key, value) -> {
            if (modifications.containsKey(key) && modifications.get(key).equals(value)) {
                modifications.remove(key);
            }
        });
        return modifications.keySet();
    }

    /**
     * Restores the given files from the given commit and stages them.
     * @param commit the {@link Commit} from which to restore the files.
     * @param files the {@link Set} of files to restore.
     */
    private void restoreAndStageCommitFiles(Commit commit, Set<String> files) {
        Add add = new Add(vcs);
        files.forEach(file -> {
            Path path = Path.of(file);
            restoreFile(commit, path);
            String basename = basename(path);
            add.add(basename);
        });
    }

    /**
     * Returns the files deleted in {@code commit} relative to {@code base}.
     * @param base the base {@link Commit}.
     * @param commit the {@link Commit} with the deletions.
     * @return a {@link Set} containing the deletions.
     */
    private Set<String> getDeletions(Commit base, Commit commit) {
        Set<String> deletions = base.getCommitFiles().collect(Collectors.toSet());
        commit.getCommitFiles().forEach(deletions::remove);
        return deletions;
    }

    /**
     * Removes the given files from the repository and stages them for removal.
     * @param files the files to remove.
     */
    private void removeAndStageFiles(Set<String> files) {
        Rm rm = new Rm(vcs);
        files.forEach(rm::rm);
    }

    /**
     * Returns a message for the merge commit.
     * @param branchName the name of the branch to merge into the current branch.
     * @return a message for the merge commit.
     */
    private String getMergeCommitMessage(String branchName) {
        return "Merge " + branchName + " into " + vcs.getCurrentBranch();
    }

    /**
     * Returns a message for merge conflicts.
     * @param conflicts the {@link Set} of conflicting files.
     * @return a message for the given conflicts.
     */
    private String getConflictMessage(Set<String> conflicts) {
        StringBuilder sb = new StringBuilder("Merge conflicts detected in:\n");
        conflicts.forEach(conflict -> {
            sb.append(conflict);
            sb.append("\n");
        });
        return sb.toString();
    }

    /**
     * Creates merge conflict blocks for the given conflicting files and writes them to the repository. See
     * {@link Merge#createMergeConflictBlock(String, String, String)} for what a merge conflict block is.
     * @param head the current branch head.
     * @param other the branch head of {@code branchName}.
     * @param conflicts the {@link Set} of conflicting files.
     * @param branchName the name of the branch to merge into the current branch.
     */
    private void mergeConflicts(Commit head, Commit other, Set<String> conflicts, String branchName) {
        conflicts.forEach(conflict -> {
            Path path = Path.of(conflict);
            String headContents = getContents(head, path);
            String otherContents = getContents(other, path);
            String mergeConflictBlock = createMergeConflictBlock(headContents, otherContents, branchName);
            write(mergeConflictBlock, path);
        });
    }

    /**
     * Returns the contents of the given file from the given commit.
     * @param commit the {@link Commit} from which to return the file contents.
     * @param file the path of the file.
     * @return the contents of the given file.
     */
    private String getContents(Commit commit, Path file) {
        String sha1 = commit.getFileHash(file);
        if (sha1 == null) {
            return "";
        }
        Blob blob = getCommittedBlob(sha1);
        return blob.getContents();
    }

    /**
     * Creates a merge conflict block from the given contents. A merge conflict block contains markers that separate
     * and label the conflicting versions.
     * @param headContents the contents of the file in the current branch head.
     * @param otherContents the contents of the file in {@code branchName}.
     * @param branchName the name of the branch to merge into the current branch.
     * @return the merge conflict block.
     */
    private String createMergeConflictBlock(String headContents, String otherContents, String branchName) {
        return "<<<<<<< HEAD\n"
                + headContents
                + "=======\n"
                + otherContents
                + ">>>>>>> " + branchName + "\n";
    }
}
