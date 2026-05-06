package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gluedtokeyboard.vcs.util.General.*;

/**
 * Methods to merge branches.
 */
public class Merge extends BaseCommand {
    /**
     * Used to stage files when creating a merge commit.
     */
    private final Add add;

    /**
     * Used to delete files and stage removals when creating a merge commit.
     */
    private final Rm rm;

    /**
     * Used to create a merge commit.
     */
    private final CommitCommand commitCommand;

    /**
     * Used to check out the given branch when fast-forwarding.
     */
    private final Checkout checkout;

    /**
     * Used to convert repository-relative paths to absolute paths.
     */
    private final GeneralService generalService;

    /**
     * Used to get commits in BFS order.
     */
    private final CommitService commitService;

    /**
     * Used to restore files and check if it is safe to merge the given branch.
     */
    private final WorkingTreeService workingTreeService;

    /**
     * Stores the given interface implementations, command objects, and service objects.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param add an {@link Add} object initialized with the same interface implementations as this object.
     * @param rm an {@link Rm} object initialized with the same interface implementations as this object.
     * @param commitCommand a {@link CommitCommand} object initialized with the same interface implementations as this
     *                      object.
     * @param checkout a {@link Checkout} object initialized with the same interface implementations as this object.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     * @param commitService a {@link CommitService} object initialized with the same interface implementations as this
     *                       object.
     * @param workingTreeService a {@link WorkingTreeService} object initialized with the same interface implementations
     *                           as this object.
     */
    public Merge(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, Add add, Rm rm, CommitCommand commitCommand, Checkout checkout,
            GeneralService generalService, CommitService commitService, WorkingTreeService workingTreeService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.add = add;
        this.rm = rm;
        this.commitCommand = commitCommand;
        this.checkout = checkout;
        this.generalService = generalService;
        this.commitService = commitService;
        this.workingTreeService = workingTreeService;
    }

    /**
     * Merges the given branch into the current branch.
     * @param ctx the repository context.
     * @param branchName the name of the branch.
     */
    public void merge(VcsContext ctx, String branchName) {
        if (branchName.equals(ctx.vcs().getCurrentBranch())) {
            throw new RuntimeException("You are already on branch " + branchName + ".");
        }
        if (!ctx.vcs().containsBranch(branchName)) {
            throw new RuntimeException("Branch " + branchName + " does not exist.");
        }
        String otherSha1 = ctx.vcs().getBranchHead(branchName);
        Commit other = commitStore.load(ctx.repoRoot(), otherSha1);
        String currentSha1 = ctx.vcs().getCurrentBranchHead();
        Commit current = commitStore.load(ctx.repoRoot(), currentSha1);
        Commit splitPoint = getSplitPoint(ctx, current, other);
        checkMergeNeeded(ctx, current, other, splitPoint, branchName);
        workingTreeService.checkSafety(ctx, other);
        mergeCommits(ctx, current, other, splitPoint, branchName);
    }

    /**
     * Returns the split point between the two commits.
     * @param ctx the repository context.
     * @param commit1 the first {@link Commit}.
     * @param commit2 the second {@link Commit}.
     * @return the split point between the two commits.
     */
    private Commit getSplitPoint(VcsContext ctx, Commit commit1, Commit commit2) {
        Set<String> commit1Ancestors = new HashSet<>();
        commitService.getCommitsInBFSOrder(ctx, commit1).forEach(commit1Ancestors::add);
        Iterable<String> commit2Ancestors = commitService.getCommitsInBFSOrder(ctx, commit2);
        for (String commit2Ancestor : commit2Ancestors) {
            if (commit1Ancestors.contains(commit2Ancestor)) {
                return commitStore.load(ctx.repoRoot(),commit2Ancestor);
            }
        }
        throw new RuntimeException(
                "Shouldn't be here. The split point between two commits is guaranteed to exist as all commits have the "
                + "initial commit as a common ancestor."
        );
    }

    /**
     * Checks if a merge is needed between the two commits.
     * @param ctx the repository context.
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
    private void checkMergeNeeded(VcsContext ctx, Commit current, Commit other, Commit splitPoint, String branchName) {
        String splitPointSha1 = splitPoint.getSha1();
        String currentSha1 = current.getSha1();
        String otherSha1 = other.getSha1();
        if (splitPointSha1.equals(currentSha1)) {
            checkout.checkoutBranch(ctx, branchName);
            ctx.vcs().setCurrentBranchHead(otherSha1);
            throw new RuntimeException("Current branch fast-forwarded.");
        } else if (splitPointSha1.equals(otherSha1)) {
            throw new RuntimeException("The given branch is an ancestor of the current branch.");
        }
    }

    /**
     * Merges the two commits.
     * @param ctx the repository context.
     * @param current the current branch head.
     * @param other the branch head of {@code branchName}.
     * @param splitPoint the split point between the two commits.
     * @param branchName the name of the branch to merge into the current branch.
     */
    private void mergeCommits(VcsContext ctx, Commit current, Commit other, Commit splitPoint, String branchName) {
        // Handle modifications.
        Set<String> currentModifications = getModifications(splitPoint, current);
        Set<String> otherModifications = getModifications(splitPoint, other);
        Set<String> otherExclusiveModifications = setDifference(otherModifications, currentModifications);
        restoreAndStageCommitFiles(ctx, other, otherExclusiveModifications);
        Set<String> modificationConflicts = setIntersection(currentModifications, otherModifications);
        // Handle deletions.
        Set<String> currentDeletions = getDeletions(splitPoint, current);
        Set<String> otherDeletions = getDeletions(splitPoint, other);
        Set<String> toBeDeleted = // Files deleted in the other commit but unmodified in the current commit.
                setDifference(otherDeletions, currentDeletions, currentModifications);
        Stream<String> toBeDeletedAbsolutePaths = toBeDeleted.stream()
                .map(path -> generalService.getAbsolutePath(ctx.repoRoot(), path));
        removeAndStageFiles(ctx, toBeDeletedAbsolutePaths);
        Set<String> deletionConflicts = // Files that are deleted in one commit but modified in the other.
                setUnion(
                        setIntersection(currentDeletions, otherModifications),
                        setIntersection(otherDeletions, currentModifications)
                );
        // Handle merge conflicts if any, and make a merge commit.
        Set<String> conflicts = setUnion(modificationConflicts, deletionConflicts);
        if (!conflicts.isEmpty()) {
            mergeConflicts(ctx.repoRoot(), current, other, conflicts, branchName);
        }
        createMergeCommit(ctx, branchName);
        if (!conflicts.isEmpty()) {
            String conflictMessage = getConflictMessage(conflicts);
            throw new RuntimeException(conflictMessage);
        }
    }

    /**
     * Creates a merge commit.
     * @param ctx the repository context.
     * @param branchName the name of the branch to merge into the current branch.
     */
    private void createMergeCommit(VcsContext ctx, String branchName) {
        String branchHead = ctx.vcs().getBranchHead(branchName);
        String commitMessage = getMergeCommitMessage(ctx, branchName);
        commitCommand.commit(ctx, branchHead, commitMessage);
    }

    /**
     * Returns the modifications made in {@code commit} relative to {@code base}.
     * @param base the base {@link Commit}.
     * @param commit the {@link Commit} with the modifications.
     * @return a {@link Set} containing the modifications.
     */
    private static Set<String> getModifications(Commit base, Commit commit) {
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
     * @param ctx the repository context.
     * @param commit the {@link Commit} from which to restore the files.
     * @param commitFiles the {@link Set} of files to restore and stage from the given commit.
     */
    private void restoreAndStageCommitFiles(VcsContext ctx, Commit commit, Set<String> commitFiles) {
        commitFiles.forEach(repoRelativePath -> {
            String absolutePath = generalService.getAbsolutePath(ctx.repoRoot(), repoRelativePath);
            workingTreeService.restoreFile(ctx, commit, absolutePath);
            add.add(ctx, absolutePath);
        });
    }

    /**
     * Returns the files deleted in {@code commit} relative to {@code base}.
     * @param base the base {@link Commit}.
     * @param commit the {@link Commit} with the deletions.
     * @return a {@link Set} containing the deletions.
     */
    private static Set<String> getDeletions(Commit base, Commit commit) {
        Set<String> deletions = base.getCommitFiles().collect(Collectors.toSet());
        commit.getCommitFiles().forEach(deletions::remove);
        return deletions;
    }

    /**
     * Removes the given files from the repository and stages them for removal.
     * @param ctx the repository context.
     * @param files the files to remove.
     */
    private void removeAndStageFiles(VcsContext ctx, Stream<String> files) {
        files.forEach(file -> rm.rm(ctx, file));
    }

    /**
     * Returns a message for the merge commit.
     * @param ctx the repository context.
     * @param branchName the name of the branch to merge into the current branch.
     * @return a message for the merge commit.
     */
    private String getMergeCommitMessage(VcsContext ctx, String branchName) {
        return "Merge " + branchName + " into " + ctx.vcs().getCurrentBranch();
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
     * @param repoRoot the repository root.
     * @param head the current branch head.
     * @param other the branch head of {@code branchName}.
     * @param conflicts the {@link Set} of conflicting files.
     * @param branchName the name of the branch to merge into the current branch.
     */
    private void mergeConflicts(String repoRoot, Commit head, Commit other, Set<String> conflicts, String branchName) {
        conflicts.forEach(conflict -> {
            String headContents = getContents(repoRoot, head, conflict);
            String otherContents = getContents(repoRoot, other, conflict);
            String mergeConflictBlock = createMergeConflictBlock(headContents, otherContents, branchName);
            String conflictAbsolutePath = generalService.getAbsolutePath(repoRoot, conflict);
            storage.write(mergeConflictBlock, conflictAbsolutePath);
        });
    }

    /**
     * Returns the contents of the given file from the given commit.
     * @param repoRoot the repository root.
     * @param commit the {@link Commit} from which to return the file contents.
     * @param file the path of the file.
     * @return the contents of the given file.
     */
    private String getContents(String repoRoot, Commit commit, String file) {
        String sha1 = commit.getFileHash(file);
        if (sha1 == null) {
            return "";
        }
        Blob blob = blobStore.load(repoRoot, sha1);
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
    private static String createMergeConflictBlock(String headContents, String otherContents, String branchName) {
        return "<<<<<<< HEAD\n"
                + headContents
                + "=======\n"
                + otherContents
                + ">>>>>>> " + branchName + "\n";
    }
}
