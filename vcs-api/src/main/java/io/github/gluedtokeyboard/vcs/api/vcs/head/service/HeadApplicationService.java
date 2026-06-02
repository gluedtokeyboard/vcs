package io.github.gluedtokeyboard.vcs.api.vcs.head.service;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.service.BranchService;
import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Checkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service methods for head.
 */
@Service
@RequiredArgsConstructor
public class HeadApplicationService {
    private final RepoContext repoContext;
    private final BranchService branchService;
    private final Checkout checkout;

    /**
     * Checks out the given branch in the given repository.
     *
     * @param repoId the repository ID
     * @param branchId the branch ID
     */
    public void checkoutBranch(UUID repoId, UUID branchId) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        String branchName = branchService.getBranchById(branchId).getName();
        checkout.checkoutBranch(ctx, branchName);
    }
}
