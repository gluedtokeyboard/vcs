package io.github.gluedtokeyboard.vcs.api.vcs.branch.repository;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link Branch}.
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
}
