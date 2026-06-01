package io.github.gluedtokeyboard.vcs.api.vcs.branch.service;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.repository.BranchRepository;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
