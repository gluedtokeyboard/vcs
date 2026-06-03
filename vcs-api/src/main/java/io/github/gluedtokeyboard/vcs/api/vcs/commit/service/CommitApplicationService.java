package io.github.gluedtokeyboard.vcs.api.vcs.commit.service;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.service.BranchService;
import io.github.gluedtokeyboard.vcs.api.vcs.commit.model.CommitType;
import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.api.vcs.common.infrastructure.context.InfrastructureContext;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.CommitCommand;
import io.github.gluedtokeyboard.vcs.core.service.command.Log;
import io.github.gluedtokeyboard.vcs.core.service.command.Merge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Commit service methods.
 */
@Service
@RequiredArgsConstructor
public class CommitApplicationService {
    private final RepoContext repoContext;
    private final BranchService branchService;
    private final InfrastructureContext infraCtx;
    private final CommitCommand commitCommand;
    private final Merge merge;
    private final Log log;

    /**
     * Creates a commit in the given repository.
     *
     * @param repoId the repository ID
     * @param type the commit type
     * @param message the commit message in case of a normal commit
     * @param branchId the branch ID in case of a merge commit
     * @return the newly created commit
     */
    public Commit commit(UUID repoId, CommitType type, String message, UUID branchId) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        switch (type) {
            case CommitType.NORMAL -> commitCommand.commit(ctx, message);
            case CommitType.MERGE -> merge.merge(
                    ctx,
                    branchService
                        .getBranchById(branchId)
                        .getName()
            );
        }
        return getLatestCommit(ctx);
    }

    /**
     * Returns a list of commits starting from the HEAD, following their first parents.
     *
     * @param repoId the repository ID
     * @return a list of commits starting from the HEAD, following their first parents
     */
    public List<Commit> log(UUID repoId) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        return log.log(ctx);
    }

    /**
     * Returns the latest commit from the given repository.
     *
     * @param ctx the repository context
     * @return the latest commit from the given repository
     */
    private Commit getLatestCommit(VcsContext ctx) {
        String head = ctx.vcs().getCurrentBranchHead();
        return infraCtx
                .commitStore()
                .load(ctx.repoRoot(), head);
    }
}
