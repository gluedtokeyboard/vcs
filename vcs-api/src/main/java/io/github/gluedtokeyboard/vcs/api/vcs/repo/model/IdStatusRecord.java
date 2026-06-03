package io.github.gluedtokeyboard.vcs.api.vcs.repo.model;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;

import java.util.List;

/**
 * Stores {@link io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord} fields with IDs.
 *
 * @param currentBranch the current branch
 * @param branches all the branches of the repository
 * @param stagedAdditions the files staged for addition
 * @param stagedDeletions the files staged for deletion
 * @param unstagedModifications the files modified but not staged for addition
 * @param unstagedDeletions the files deleted but not staged for deletion
 * @param untrackedFiles the files that are not tracked by the repository
 */
public record IdStatusRecord(
        Branch currentBranch, List<Branch> branches, List<StagingEntry> stagedAdditions,
        List<StagingEntry> stagedDeletions, List<FilesystemNode> unstagedModifications,
        List<String> unstagedDeletions, List<FilesystemNode> untrackedFiles) {
}
