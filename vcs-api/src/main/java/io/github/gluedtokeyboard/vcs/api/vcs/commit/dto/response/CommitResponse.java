package io.github.gluedtokeyboard.vcs.api.vcs.commit.dto.response;

import io.github.gluedtokeyboard.vcs.api.vcs.commit.model.CommitType;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;

import java.util.Date;
import java.util.UUID;

/**
 * Response DTO for commits.
 *
 * @param repoId the repository ID of the commit
 * @param hash the commit hash
 * @param message the commit message
 * @param timestamp the commit timestamp
 * @param parent1 the first parent of the commit
 * @param parent2 the second parent of the commit in case of a merge commit
 * @param type the commit type
 */
public record CommitResponse(
        UUID repoId, String hash, String message, Date timestamp, String parent1, String parent2, CommitType type) {
    /**
     * Creates a {@link CommitResponse} from a {@link Commit} and a repository ID.
     *
     * @param commit a {@link Commit}
     * @param repoId the repository ID
     * @return the {@link CommitResponse} created from the given {@link Commit} and repository ID
     */
    public static CommitResponse from(Commit commit, UUID repoId) {
        CommitType type = commit.getParent2().equals(CommitService.NULL_COMMIT) ? CommitType.NORMAL : CommitType.MERGE;
        return new CommitResponse(
                repoId, commit.getSha1(), commit.getMessage(), commit.getTimestamp(), commit.getParent1(),
                commit.getParent2(), type);
    }
}
