package io.github.gluedtokeyboard.vcs.api.vcs.repo.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.repository.RepoRepository;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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

    /**
     * Returns the repository present at the given location.
     *
     * @param repoRoot the repository root
     * @return the repository present at the given location
     * @throws NoSuchElementException if a repository is not present at the given location
     */
    public Repo getRepo(String repoRoot) {
        FilesystemNode repoRootNode = nodeService.getNode(repoRoot);
        return getRepo(() -> repoRepository.findByRepoRootId(repoRootNode.getId()));
    }

    /**
     * Returns the {@link Repo} returned by the given supplier.
     *
     * @param repoSupplier a {@link Supplier} that returns an optional {@link Repo}
     * @return the {@link Repo} obtained from the supplier if present
     * @throws NoSuchElementException if the supplier returns an empty {@link Optional}
     */
    private Repo getRepo(Supplier<Optional<Repo>> repoSupplier) {
        return repoSupplier.get().orElseThrow(NoSuchElementException::new);
    }
}
