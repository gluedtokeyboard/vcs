package io.github.gluedtokeyboard.vcs.api.vcs.branch.service;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service methods for branches.
 */
@Service
@RequiredArgsConstructor
public class BranchApplicationService {
    private final BranchService branchService;
    private final IdBranch branch;
    private final RepoContext repoContext;

    /**
     * Creates a branch in the given repository.
     *
     * @param repoId the repository ID
     * @param branchName the branch name
     * @return the newly created branch
     */
    public Branch createBranch(UUID repoId, String branchName) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        branch.createBranch(ctx, branchName);
        Branch branch = branchService.getBranchByRepoIdAndName(repoId, branchName);
        repoContext.storeVcs(ctx);
        return branch;
    }

    /**
     * Removes the given branch.
     *
     * @param branchId the branch ID
     */
    public void removeBranch(UUID branchId) {
        Branch branchEntity = branchService.getBranchById(branchId);
        String repoRoot = branchEntity.getRepo().getRepoRoot().getPath();
        VcsContext ctx = repoContext.getContextByDirectory(repoRoot);
        String branchName = branchEntity.getName();
        branch.removeBranch(ctx, branchName);
        repoContext.storeVcs(ctx);
    }
}
