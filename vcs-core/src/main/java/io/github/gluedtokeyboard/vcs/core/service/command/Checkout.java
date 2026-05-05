package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * Methods for checking out files and branches.
 */
public class Checkout extends BaseCommand {
    /**
     * Used to convert absolute paths to repository-relative paths.
     */
    private final GeneralService generalService;

    /**
     * Used to restore files and apply commits safely.
     */
    private final WorkingTreeService workingTreeService;

    /**
     * Stores the given interface implementations and service objects.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     * @param workingTreeService a {@link WorkingTreeService} object initialized with the same interface implementations
     *                           as this object.
     */
    public Checkout(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService,
            WorkingTreeService workingTreeService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.generalService = generalService;
        this.workingTreeService = workingTreeService;
    }

    /**
     * Checks out the given file from the head of the current branch.
     *
     * @param ctx the repository context.
     * @param file the path of the file.
     * @throws RuntimeException if the given file isn't found in the head.
     */
    public void checkout(VcsContext ctx, String file) {
        checkout(ctx, ctx.vcs().getCurrentBranchHead(), file);
    }

    /**
     * Checks out the given file from the given commit.
     *
     * @param ctx the repository context.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @param file     the path of the file.
     * @throws RuntimeException if a commit with the given commit ID does not exist, or the given file isn't found in
     *                          the commit.
     */
    public void checkout(VcsContext ctx, String commitId, String file) {
        Commit commit = commitStore.load(ctx.repoRoot(), commitId);
        if (commit == null) {
            throw new RuntimeException("Commit with id " + commitId + " not found");
        }
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), file);
        if (!commit.contains(repoRelativePath)) {
            throw new RuntimeException("File " + file + " not found in commit " + commit.getSha1());
        }
        workingTreeService.restoreFile(ctx, commit, file);
    }

    /**
     * Checks out the given branch.
     *
     * @param ctx the repository context.
     * @param branch the branch name.
     * @throws RuntimeException if the branch does not exist, or if it is the current branch.
     */
    public void checkoutBranch(VcsContext ctx, String branch) {
        if (!ctx.vcs().containsBranch(branch)) {
            throw new RuntimeException("Branch " + branch + " does not exist");
        }
        if (ctx.vcs().getCurrentBranch().equals(branch)) {
            throw new RuntimeException("Branch " + branch + " is already the current branch");
        }
        String hash = ctx.vcs().getBranchHead(branch);
        Commit commit = commitStore.load(ctx.repoRoot(), hash);
        workingTreeService.applyCommitSafely(ctx, commit);
        ctx.vcs().setCurrentBranch(branch);
    }
}
