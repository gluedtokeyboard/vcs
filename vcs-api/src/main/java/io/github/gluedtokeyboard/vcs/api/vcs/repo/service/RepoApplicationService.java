package io.github.gluedtokeyboard.vcs.api.vcs.repo.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.service.BranchService;
import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.model.IdStatusRecord;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.repository.RepoRepository;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.service.StagingService;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Status;
import io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static io.github.gluedtokeyboard.vcs.util.General.map;

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

    private final GeneralService generalService;
    private final RepoService repoService;
    private final RepoContext repoContext;
    private final BranchService branchService;
    private final StagingService stagingService;
    private final Status status;

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

    /**
     * Searches for a repository upwards from the given directory.
     *
     * @param directoryId the directory ID
     * @return the repository found
     * @throws java.util.NoSuchElementException if a repository could not be found
     */
    public Repo resolveRepo(UUID directoryId) {
        VcsContext ctx = repoContext.getContextByDirectoryId(directoryId);
        return repoService.getRepo(ctx.repoRoot());
    }

    /**
     * Returns the status of the given repository.
     *
     * @param repoId the repository ID
     * @return the status of the given repository
     */
    public IdStatusRecord status(UUID repoId) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        StatusRecord statusRecord = status.status(ctx);
        Branch currentBranch = branchService.getBranchByRepoIdAndName(repoId, statusRecord.currentBranch());
        List<Branch> branches = map(
                statusRecord.branches(),
                branch -> branchService.getBranchByRepoIdAndName(repoId, branch)
        );
        List<StagingEntry> stagedAdditions = map(
                statusRecord.stagedAdditions(),
                absolutePath -> {
                    String relativePath = generalService.getRepoRelativePath(ctx.repoRoot(), absolutePath);
                    return stagingService.getEntry(repoId, relativePath);
                }
        );
        List<StagingEntry> stagedDeletions = map(
                statusRecord.stagedDeletions(),
                absolutePath -> {
                    String relativePath = generalService.getRepoRelativePath(ctx.repoRoot(), absolutePath);
                    return stagingService.getEntry(repoId, relativePath);
                }
        );
        List<FilesystemNode> unstagedModifications = map(
                statusRecord.unstagedModifications(),
                nodeService::getNode
        );
        List<String> unstagedDeletions = statusRecord.unstagedDeletions().stream().toList();
        List<FilesystemNode> untrackedFiles = map(
                statusRecord.untrackedFiles(),
                nodeService::getNode
        );
        IdStatusRecord status = new IdStatusRecord(
                currentBranch, branches, stagedAdditions, stagedDeletions, unstagedModifications, unstagedDeletions,
                untrackedFiles);
        repoContext.storeVcs(ctx);
        return status;
    }
}
