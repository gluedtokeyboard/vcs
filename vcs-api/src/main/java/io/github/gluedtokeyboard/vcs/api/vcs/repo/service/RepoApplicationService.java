package io.github.gluedtokeyboard.vcs.api.vcs.repo.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.repository.RepoRepository;
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
}
