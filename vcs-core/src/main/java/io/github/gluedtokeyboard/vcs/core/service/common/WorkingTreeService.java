package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.Status;
import io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.Set;

import static io.github.gluedtokeyboard.vcs.util.General.setIntersection;

/**
 * Methods to restore files, and safely apply commits.
 */
public final class WorkingTreeService extends BaseService {
    /**
     * Used to get unstaged modifications, unstaged deletions, and untracked files.
     */
    private final Status status;

    /**
     * Used to convert absolute paths to repository-relative paths and traverse repository files.
     */
    private final GeneralService generalService;

    /**
     * Used to get the absolute file paths of commit files.
     */
    private final CommitService commitService;

    /**
     * Stores the given interface implementations, a {@link Status} object, and service objects.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param status a {@link Status} object initialized with the same interface implementations as this object.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     * @param commitService a {@link CommitService} object initialized with the same interface implementations as this
     *                       object.
     */
    public WorkingTreeService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, Status status, GeneralService generalService,
            CommitService commitService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.status = status;
        this.generalService = generalService;
        this.commitService = commitService;
    }

    /**
     * Restores the given file from the given commit.
     * @param ctx the repository context.
     * @param commit the {@link Commit} object.
     * @param path the path of the file.
     */
    public void restoreFile(VcsContext ctx, Commit commit, String path) {
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        String sha1 = commit.getFileHash(repoRelativePath);
        Blob blob = blobStore.load(ctx.repoRoot(), sha1);
        blob.writeToFile(path);
    }

    /**
     * Applies the commit only if no data in the repository would be lost.
     * @param ctx the repository context.
     * @param commitPrefix a prefix of the commit's hash.
     */
    public void applyCommitSafely(VcsContext ctx, String commitPrefix) {
        Commit commit = commitStore.load(ctx.repoRoot(), commitPrefix);
        applyCommitSafely(ctx, commit);
    }

    /**
     * Applies the commit only if no data in the repository would be lost.
     * @param ctx the repository context.
     * @param commit the {@link Commit} to apply.
     */
    public void applyCommitSafely(VcsContext ctx, Commit commit) {
        checkSafety(ctx, commit);
        removeTrackedFiles(ctx);
        applyCommit(ctx, commit);
    }

    /**
     * Checks if it is safe to apply the given commit.
     * @param ctx the repository context.
     * @param commit the {@link Commit} to apply.
     * @throws RuntimeException if:
     * <ul>
     *     <li>The staging area isn't empty.</li>
     *     <li>There are unstaged modifications present.</li>
     *     <li>There are untracked files present that would be overwritten.</li>
     * </ul>
     */
    public void checkSafety(VcsContext ctx, Commit commit) {
        if (!ctx.vcs().stagingAreaIsEmpty()) {
            throw new RuntimeException("The staging area isn't empty");
        }
        StatusRecord statusRecord = status.status(ctx);
        if (hasUnstagedModifications(statusRecord)) {
            throw new RuntimeException("There are unstaged modifications present");
        }
        if (hasUntrackedConflicts(ctx.repoRoot(), statusRecord, commit)) {
            throw new RuntimeException("There are untracked files present that would be overwritten");
        }
    }

    /**
     * Checks if the repository has unstaged modifications.
     * @param statusRecord a {@link StatusRecord} object containing the repository status.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUnstagedModifications(StatusRecord statusRecord) {
        return !(statusRecord.unstagedModifications().isEmpty() && statusRecord.unstagedDeletions().isEmpty());
    }

    /**
     * Checks if the repository has untracked files that would be overwritten while checking out the given
     * {@link Commit}.
     * @param repoRoot the repository root.
     * @param statusRecord a {@link StatusRecord} object containing the repository status.
     * @param commit the {@link Commit} to check out.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private boolean hasUntrackedConflicts(String repoRoot, StatusRecord statusRecord, Commit commit) {
        Set<String> commitFilesAbsolutePaths = commitService.getCommitFilesAbsolutePaths(repoRoot, commit);
        Set<String> untrackedFiles = statusRecord.untrackedFiles();
        return !setIntersection(commitFilesAbsolutePaths, untrackedFiles).isEmpty();
    }

    /**
     * Removes the files tracked in the head commit.
     * @param ctx the repository context.
     */
    private void removeTrackedFiles(VcsContext ctx) {
        String currentBranchHead = ctx.vcs().getCurrentBranchHead();
        Commit head = commitStore.load(ctx.repoRoot(), currentBranchHead);
        commitService.getCommitFilesAbsolutePaths(ctx.repoRoot(), head).forEach(storage::rm);
        removeEmptyDirectories(ctx.repoRoot());
    }

    /**
     * Removes empty directories in the repository.
     * @param repoRoot the repository root.
     */
    private void removeEmptyDirectories(String repoRoot) {
        generalService.walkRepoDirsPost(repoRoot, path -> {
            try {
                storage.rm(path);
            } catch (IllegalStateException e) {
                /* Directory not empty, so do nothing. */
            }
        });
    }

    /**
     * Applies the given {@link Commit}.
     * @param ctx the repository context.
     * @param commit the {@link Commit} to apply.
     */
    private void applyCommit(VcsContext ctx, Commit commit) {
        commitService.getCommitFilesAbsolutePaths(ctx.repoRoot(), commit)
                .forEach(path -> restoreFile(ctx, commit, path));
    }
}
