package io.github.gluedtokeyboard.vcs.api.vcs.repo.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.repository.RepoRepository;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service methods for {@link Repo}.
 */
@Service
@RequiredArgsConstructor
public class RepoService {
    /**
     * Used to interact with the database.
     */
    private final RepoRepository repoRepository;

    /**
     * Used to retrieve the filesystem nodes corresponding to repository roots.
     */
    private final NodeService nodeService;

    /**
     * Used to obtain the repository folder.
     */
    private final VcsLayout vcsLayout;

    /**
     * Creates a {@link Repo} with the given repo root.
     *
     * @param repoRoot the repo root
     * @return the newly created {@link Repo}
     */
    public Repo createRepo(String repoRoot) {
        FilesystemNode repoRootNode = nodeService.getNode(repoRoot);
        String repoFolder = vcsLayout.getRepoFolder(repoRoot);
        FilesystemNode repoFolderNode = nodeService.getNode(repoFolder);
        return repoRepository.save(new Repo(repoRootNode, repoFolderNode));
    }
}
