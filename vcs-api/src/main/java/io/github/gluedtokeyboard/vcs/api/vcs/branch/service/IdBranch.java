package io.github.gluedtokeyboard.vcs.api.vcs.branch.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.Branch;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import org.springframework.stereotype.Service;

/**
 * {@link Branch} with ID support.
 */
@Service
public class IdBranch extends Branch {
    private final BranchService branchService;

    /**
     * Stores the given interface implementations.
     *
     * @param storage     an {@link IdStorage}
     * @param vcsLayout   a {@link VcsLayout} implementation
     * @param vcsStore    a {@link VcsStore} implementation
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation
     * @param branchService the object containing {@link io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch}
     *                      service methods
     */
    public IdBranch(
            IdStorage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, BranchService branchService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.branchService = branchService;
    }

    @Override
    public void createBranch(VcsContext ctx, String branchName) {
        super.createBranch(ctx, branchName);
        branchService.createBranch(ctx.repoRoot(), branchName);
    }

    @Override
    public void removeBranch(VcsContext ctx, String branchName) {
        super.removeBranch(ctx, branchName);
        branchService.removeBranch(ctx.repoRoot(), branchName);
    }
}
