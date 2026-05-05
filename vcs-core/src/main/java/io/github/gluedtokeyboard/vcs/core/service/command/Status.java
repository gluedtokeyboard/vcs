package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gluedtokeyboard.vcs.util.General.setDifference;

/**
 * Methods for getting the status of the repository.
 */
public class Status extends BaseCommand {
    /**
     * Used to traverse the repository files, and to convert absolute paths to repository-relative paths.
     */
    private final GeneralService generalService;

    /**
     * Used get a map from the absolute paths of the staged files to their hashes.
     */
    private final StagingAreaService stagingAreaService;

    /**
     * Used to get the latest commit of the current branch and its files.
     */
    private final CommitService commitService;

    /**
     * Stores the given interface implementations and service objects.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore <Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     * @param stagingAreaService a {@link StagingAreaService} object initialized with the same interface implementations
     *                           as this object.
     * @param commitService a {@link CommitService} object initialized with the same interface implementations as this
     *                       object.
     */
    public Status(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService,
            StagingAreaService stagingAreaService, CommitService commitService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.generalService = generalService;
        this.stagingAreaService = stagingAreaService;
        this.commitService = commitService;
    }

    /**
     * The status is composed of the following:
     * <ul>
     *     <li>A list of branches</li>
     *     <li>The current branch</li>
     *     <li>The files staged for addition</li>
     *     <li>The files staged for deletion</li>
     *     <li>Unstaged modifications</li>
     *     <li>Unstaged deletions</li>
     *     <li>Untracked files</li>
     * </ul>
     * @param ctx the repository context.
     * @return A {@link StatusRecord} object containing those items.
     */
    public StatusRecord status(VcsContext ctx) {
        List<String> branches = ctx.vcs().getAllBranches().toList();
        String currentBranch = ctx.vcs().getCurrentBranch();
        Map<String, String> stagedFilesAbsolutePaths = stagingAreaService.getStagedFilesAbsolutePaths(ctx);
        Set<String> stagedAdditions = getStagedAdditions(stagedFilesAbsolutePaths);
        Set<String> stagedDeletions = getStagedDeletions(stagedFilesAbsolutePaths);
        Set<String> unstagedModifications = new HashSet<>();
        Set<String> untrackedFiles = new HashSet<>();
        Set<String> cwdFiles = new HashSet<>();
        Commit latestCommit = commitService.getLatestCommitCurrentBranch(ctx);
        Set<String> latestCommitFilesAbsolutePaths = getCommitFilesAbsolutePaths(ctx.repoRoot(), latestCommit);
        generalService.walkRepoFiles(ctx.repoRoot(), file -> {
            if (isUnstagedMod(ctx, latestCommit, latestCommitFilesAbsolutePaths, file)) {
                unstagedModifications.add(file);
            } else if (isUntracked(ctx, stagedDeletions, latestCommitFilesAbsolutePaths, file)) {
                untrackedFiles.add(file);
            }
            cwdFiles.add(file);
        });
        Set<String> unstagedDeletions = new HashSet<>();
        unstagedDeletions.addAll(setDifference(stagedAdditions, cwdFiles));
        unstagedDeletions.addAll(setDifference(latestCommitFilesAbsolutePaths, cwdFiles, stagedDeletions));
        return new StatusRecord(
                branches, currentBranch, stagedAdditions, stagedDeletions, unstagedModifications, unstagedDeletions,
                untrackedFiles
        );
    }

    /**
     * Returns the absolute paths of the files tracked by the given commit.
     * @param repoRoot the repository root.
     * @param commit a {@link Commit} object.
     * @return the absolute paths of the files tracked by the given commit.
     */
    private Set<String> getCommitFilesAbsolutePaths(String repoRoot, Commit commit) {
        if (commit == null) {
            return Collections.emptySet();
        }
        return commitService.getCommitFilesAbsolutePaths(repoRoot, commit);
    }

    /**
     * Checks if the given file has been modified but hasn't been staged for addition.
     * @param ctx the repository context.
     * @param latestCommit the latest {@link Commit} in the current branch.
     * @param latestCommitFilesAbsolutePaths the absolute paths of the files tracked by the latest {@link Commit} in
     *                                       the current branch.
     * @param file the path of the file.
     * @return a boolean indicating whether the given file has been modified but not staged for addition.
     */
    private boolean isUnstagedMod(
            VcsContext ctx, Commit latestCommit, Set<String> latestCommitFilesAbsolutePaths, String file) {
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), file);
        Blob blob = new Blob(storage, file);
        return latestCommitFilesAbsolutePaths.contains(file)
                && !latestCommit.getFileHash(repoRelativePath).equals(blob.getSha1())
                && !ctx.vcs().isStaged(repoRelativePath)
                || (ctx.vcs().isStaged(repoRelativePath)
                     && !ctx.vcs().getStagedSha(repoRelativePath).equals(blob.getSha1()));
    }

    /**
     * Checks if the given file is untracked.
     * @param ctx the repository context.
     * @param stagedDeletions the absolute paths of the files currently staged for deletion.
     * @param latestCommitFilesAbsolutePaths the absolute paths of the files tracked by the latest {@link Commit} in
     *                                       the current branch.
     * @param file the path of the file.
     * @return a boolean indicating whether the given file is untracked.
     */
    private boolean isUntracked(
            VcsContext ctx, Set<String> stagedDeletions, Set<String> latestCommitFilesAbsolutePaths, String file) {
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), file);
        return stagedDeletions.contains(file)
                || (!latestCommitFilesAbsolutePaths.contains(file) && !ctx.vcs().isStaged(repoRelativePath));
    }

    /**
     * Returns the files currently staged for addition.
     * @param stagedFiles all the files currently staged for addition or deletion.
     * @return a {@link Set} containing the files currently staged for addition.
     */
    private static Set<String> getStagedAdditions(Map<String, String> stagedFiles) {
        return filterStagedFiles(
                stagedFiles,
                entry -> !entry.getValue().equals(StagingAreaService.DELETION_MARKER)
        ).collect(Collectors.toSet());
    }

    /**
     * Returns the files currently staged for deletion.
     * @param stagedFiles all the files currently staged for addition or deletion.
     * @return a {@link Set} containing the files currently staged for deletion.
     */
    private static Set<String> getStagedDeletions(Map<String, String> stagedFiles) {
        return filterStagedFiles(
                stagedFiles,
                entry -> entry.getValue().equals(StagingAreaService.DELETION_MARKER)
        ).collect(Collectors.toSet());
    }

    /**
     * Filters the staged files.
     * @param filter a {@link Predicate} to filter the staged files.
     * @return a {@link Stream} containing the remaining files.
     */
    private static Stream<String> filterStagedFiles(
            Map<String, String> stagedFiles, Predicate<Map.Entry<String, String>> filter) {
        return stagedFiles
                .entrySet()
                .stream()
                .filter(filter)
                .map(Map.Entry::getKey);
    }

    /**
     * Stores all the components of the status.
     * @param branches a list of branches.
     * @param currentBranch the current branch.
     * @param stagedAdditions the files staged for addition.
     * @param stagedDeletions the files staged for deletion.
     * @param unstagedModifications the files that have been modified but haven't been staged for addition.
     * @param unstagedDeletions the files that have been removed but haven't been staged for deletion.
     * @param untrackedFiles the untracked files.
     */
    public record StatusRecord(
            List<String> branches, String currentBranch, Set<String> stagedAdditions, Set<String> stagedDeletions,
            Set<String> unstagedModifications, Set<String> unstagedDeletions, Set<String> untrackedFiles) {}
}
