package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.service;

import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
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

    /**
     * Resets given repository to the given commit.
     *
     * @param repoId the repository ID
     * @param commitPrefix a prefix of the commit's hash
     */
    public void reset(UUID repoId, String commitPrefix) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        reset.reset(ctx, commitPrefix);
    }
}
