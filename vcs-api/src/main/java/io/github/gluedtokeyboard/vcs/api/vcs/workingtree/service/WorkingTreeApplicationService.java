package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.service;

import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Checkout;
import io.github.gluedtokeyboard.vcs.core.service.command.Reset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Working tree service methods.
 */
@Service
@RequiredArgsConstructor
public class WorkingTreeApplicationService {
    private final RepoContext repoContext;
    private final Reset reset;
    private final Checkout checkout;

    /**
     * Resets given repository to the given commit.
     *
     * @param repoId the repository ID
     * @param commitPrefix a prefix of the commit's hash
     */
    public void reset(UUID repoId, String commitPrefix) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        reset.reset(ctx, commitPrefix);
        repoContext.storeVcs(ctx);
    }

    /**
     * Checks out the given file from the given commit in the given repository.
     *
     * @param repoId the repository ID
     * @param commitPrefix a prefix of the commit's hash. Defaults to HEAD if null
     * @param path the file path
     */
    public void checkoutFile(UUID repoId, String commitPrefix, String path) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        if (commitPrefix == null) {
            checkout.checkout(ctx, path);
        } else {
            checkout.checkout(ctx, commitPrefix, path);
        }
        repoContext.storeVcs(ctx);
    }
}
