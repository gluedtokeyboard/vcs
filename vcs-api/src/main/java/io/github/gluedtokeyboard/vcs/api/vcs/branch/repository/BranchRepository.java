package io.github.gluedtokeyboard.vcs.api.vcs.branch.repository;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Branch}.
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    /**
     * Retrieves the branch from the given repository with the given name.
     *
     * @param repoId the repository ID
     * @param name the branch name
     */
    Optional<Branch> findByRepoIdAndName(UUID repoId, String name);

    /**
     * Deletes the branch in the given repository with the given name.
     *
     * @param repoId the repository ID
     * @param name the branch name
     */
    void deleteByRepoIdAndName(UUID repoId, String name);
}
