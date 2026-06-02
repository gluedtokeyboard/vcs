package io.github.gluedtokeyboard.vcs.api.vcs.staging.repository;

import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link StagingEntry}.
 */
@Repository
public interface StagingEntryRepository extends JpaRepository<StagingEntry, UUID> {
}
