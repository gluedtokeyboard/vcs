package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * Methods to create/remove branches.
 */
public class Branch extends BaseCommand {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public Branch(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Creates a new branch.
     * @param ctx the repository context.
     * @param branchName the name of the branch.
     * @throws RuntimeException if the branch already exists.
     */
    public void createBranch(VcsContext ctx, String branchName) {
        if (ctx.vcs().containsBranch(branchName)) {
            throw new RuntimeException("Branch " + branchName + " already exists");
        }
        ctx.vcs().setBranchHead(branchName, ctx.vcs().getCurrentBranchHead());
    }

    /**
     * Removes an existing branch.
     * @param ctx the repository context.
     * @param branchName the name of the branch.
     * @throws RuntimeException if the branch does not exist, or if it is the current branch.
     */
    public void removeBranch(VcsContext ctx, String branchName) {
        if (!ctx.vcs().containsBranch(branchName)) {
            throw new RuntimeException("Branch " + branchName + " does not exist");
        }
        if (branchName.equals(ctx.vcs().getCurrentBranch())) {
            throw new RuntimeException("Cannot remove the current branch");
        }
        ctx.vcs().removeBranch(branchName);
    }
}
