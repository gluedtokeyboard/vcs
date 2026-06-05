package io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.response;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.model.NodePath;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.dto.response.BranchResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.model.IdStatusRecord;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.dto.response.StagingResponse;
import io.github.gluedtokeyboard.vcs.storage.EntryType;

import java.util.List;

import static io.github.gluedtokeyboard.vcs.util.General.map;

/**
 * Response DTO for status requests.
 *
 * @param currentBranch the current branch of the repository
 * @param branches all the branches of the repository
 * @param stagedAdditions the files staged for addition
 * @param stagedDeletions the files staged for deletion
 * @param unstagedModifications the files modified but not staged for addition
 * @param unstagedDeletions the files deleted but not staged for deletion
 * @param untrackedFiles the files that are not tracked by the repository
 */
public record StatusResponse(
        BranchResponse currentBranch, List<BranchResponse> branches, List<StagingResponse> stagedAdditions,
        List<StagingResponse> stagedDeletions, List<NodePath> unstagedModifications, List<String> unstagedDeletions,
        List<NodePath> untrackedFiles) {
    /**
     * Creates a {@link StatusResponse} from the given {@link IdStatusRecord}.
     *
     * @param statusRecord a {@link IdStatusRecord}
     * @return the {@link StatusResponse} created from the given {@link IdStatusRecord}
     */
    public static StatusResponse from(IdStatusRecord statusRecord) {
        return new StatusResponse(
                BranchResponse.from(statusRecord.currentBranch()),
                map(statusRecord.branches(), BranchResponse::from),
                map(statusRecord.stagedAdditions(), StagingResponse::from),
                map(statusRecord.stagedDeletions(), StagingResponse::from),
                map(statusRecord.unstagedModifications(), node -> NodePath.from(node, EntryType.FILE)),
                statusRecord.unstagedDeletions(),
                map(statusRecord.untrackedFiles(), node -> NodePath.from(node, EntryType.FILE))
        );
    }
}
