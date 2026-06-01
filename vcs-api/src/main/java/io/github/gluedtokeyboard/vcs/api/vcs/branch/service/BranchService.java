package io.github.gluedtokeyboard.vcs.api.vcs.branch.service;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.repository.BranchRepository;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Service methods for {@link Branch}.
 */
@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final RepoService repoService;

    /**
     * Creates a branch in the given repository.
     *
     * @param repoRoot the repository root
     * @param branchName the branch name
     * @return the newly created branch
     */
    public Branch createBranch(String repoRoot, String branchName) {
        Repo repo = repoService.getRepo(repoRoot);
        return branchRepository.save(new Branch(repo, branchName));
    }

    /**
     * Removes the given branch from the given repository.
     *
     * @param repoRoot the repository root
     * @param branchName the branch name
     */
    @Transactional
    public void removeBranch(String repoRoot, String branchName) {
        Repo repo = repoService.getRepo(repoRoot);
        branchRepository.deleteByRepoIdAndName(repo.getId(), branchName);
    }

    /**
     * Returns the branch with the given ID.
     *
     * @param id the branch ID
     * @return the corresponding branch
     * @throws NoSuchElementException if a branch with the given ID does not exist
     */
    public Branch getBranchById(UUID id) {
        return getBranch(() -> branchRepository.findById(id));
    }

    /**
     * Returns the branch from the given repository with the given name.
     *
     * @param repoId the repository ID
     * @param branchName the branch name
     * @return the corresponding branch
     * @throws NoSuchElementException if the branch does not exist
     */
    public Branch getBranchByRepoIdAndName(UUID repoId, String branchName) {
        return getBranch(() -> branchRepository.findByRepoIdAndName(repoId, branchName));
    }

    /**
     * Returns the {@link Branch} returned by the given supplier.
     *
     * @param branchSupplier a {@link Supplier} that returns an optional {@link Branch}
     * @return the {@link Branch} obtained from the supplier if present
     * @throws NoSuchElementException if the supplier returns an empty {@link Optional}
     */
    private Branch getBranch(Supplier<Optional<Branch>> branchSupplier) {
        return branchSupplier.get().orElseThrow(NoSuchElementException::new);
    }
}
