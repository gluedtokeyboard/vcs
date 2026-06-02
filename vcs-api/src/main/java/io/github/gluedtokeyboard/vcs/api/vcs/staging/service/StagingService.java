package io.github.gluedtokeyboard.vcs.api.vcs.staging.service;

import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoService;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.model.EntryType;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.repository.StagingEntryRepository;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Contains {@link StagingEntry} service methods.
 */
@Service
@RequiredArgsConstructor
public class StagingService {
    private final RepoService repoService;
    private final GeneralService generalService;
    private final StagingEntryRepository stagingEntryRepository;

    /**
     * Creates a staging area entry for addition.
     * @param repoRoot the repository root
     * @param path the file path
     * @return the newly created entry
     */
    public StagingEntry createAdditionEntry(String repoRoot, String path) {
        return createEntry(repoRoot, path, EntryType.ADDITION);
    }

    /**
     * Creates a staging area entry for deletion.
     * @param repoRoot the repository root
     * @param path the file path
     * @return the newly created entry
     */
    public StagingEntry createDeletionEntry(String repoRoot, String path) {
        return createEntry(repoRoot, path, EntryType.DELETION);
    }

    /**
     * Deletes the staging area entry in the given repository for the given path.
     *
     * @param repoRoot the repository root
     * @param path the file path
     */
    @Transactional
    public void deleteEntry(String repoRoot, String path) {
        Repo repo = repoService.getRepo(repoRoot);
        String repoRelativePath = generalService.getRepoRelativePath(repoRoot, path);
        stagingEntryRepository.deleteByRepoIdAndRelativePath(repo.getId(), repoRelativePath);
    }

    /**
     * Deletes all the staging area entries in the given repository.
     *
     * @param repoRoot the repository root
     */
    @Transactional
    public void deleteEntries(String repoRoot) {
        Repo repo = repoService.getRepo(repoRoot);
        stagingEntryRepository.deleteAllByRepoId(repo.getId());
    }

    /**
     * Returns the staging area entry with the given relative path from the given repository.
     *
     * @param repoId the repository ID
     * @param relativePath the repository relative path of the file
     * @return the corresponding staging area entry
     */
    public StagingEntry getEntry(UUID repoId, String relativePath) {
        return stagingEntryRepository
                .findByRepoIdAndRelativePath(repoId, relativePath)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Creates a staging area entry.
     *
     * @param repoRoot the repository root
     * @param path the file path
     * @param type the entry type
     * @return the newly created entry
     */
    private StagingEntry createEntry(String repoRoot, String path, EntryType type) {
        Repo repo = repoService.getRepo(repoRoot);
        String repoRelativePath = generalService.getRepoRelativePath(repoRoot, path);
        StagingEntry stagingEntry = new StagingEntry(repo, repoRelativePath, type);
        return stagingEntryRepository.save(stagingEntry);
    }
}
