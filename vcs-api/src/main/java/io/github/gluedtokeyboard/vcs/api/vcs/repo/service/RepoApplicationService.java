package io.github.gluedtokeyboard.vcs.api.vcs.repo.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.repository.RepoRepository;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service methods for repositories.
 */
@Service
@RequiredArgsConstructor
public class RepoApplicationService {
    /**
     * Used to initialize repositories.
     */
    private final IdInit init;

    /**
     * Used to convert repository root IDs to their paths.
     */
    private final NodeService nodeService;

    /**
     * Used to retrieve {@linkplain Repo}s by their repository root IDs.
     */
    private final RepoRepository repoRepository;

    private final RepoService repoService;
    private final RepoContext repoContext;

    /**
     * Initializes the repository at the given root.
     *
     * @param repoRootId the repository root ID
     * @return the newly initialized repository
     */
    public Repo init(UUID repoRootId) {
        String repoRoot = nodeService.getPath(repoRootId);
        init.init(repoRoot);
        return repoRepository.findByRepoRootId(repoRootId).get();
    }

    /**
     * Searches for a repository upwards from the given directory.
     *
     * @param directoryId the directory ID
     * @return the repository found
     * @throws java.util.NoSuchElementException if a repository could not be found
     */
    public Repo resolveRepo(UUID directoryId) {
        VcsContext ctx = repoContext.getContextByDirectoryId(directoryId);
        return repoService.getRepo(ctx.repoRoot());
    }
}
