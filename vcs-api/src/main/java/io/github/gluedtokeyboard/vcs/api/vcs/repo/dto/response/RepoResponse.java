package io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.response;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.model.NodeResult;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.storage.EntryType;

import java.util.UUID;

/**
 * Response DTO for repositories.
 *
 * @param id the repository ID
 * @param repoRoot the repository root along with its ID
 */
public record RepoResponse(UUID id, NodeResult repoRoot) {
    /**
     * Creates a {@link RepoResponse} from a {@link Repo}.
     *
     * @param repo a {@link Repo}
     * @return the {@link RepoResponse} created from the given {@link Repo}
     */
    public static RepoResponse from(Repo repo) {
        NodeResult nodeResult = NodeResult.from(repo.getRepoRoot(), EntryType.DIRECTORY);
        return new RepoResponse(repo.getId(), nodeResult);
    }
}
