package io.github.gluedtokeyboard.vcs.api.vcs.repo.repository;

import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Repo}.
 */
@Repository
public interface RepoRepository extends JpaRepository<Repo, UUID> {
    /**
     * Retrieves a {@link Repo} by its repository root ID.
     *
     * @param repoRootId the repository root ID
     * @return the {@link Repo} at the given root if present
     */
    Optional<Repo> findByRepoRootId(UUID repoRootId);
}
