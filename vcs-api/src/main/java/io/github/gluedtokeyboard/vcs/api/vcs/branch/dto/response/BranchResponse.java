package io.github.gluedtokeyboard.vcs.api.vcs.branch.dto.response;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;

import java.util.UUID;

/**
 * Response DTO for branches.
 *
 * @param id the branch ID
 * @param repoId the repository ID
 * @param name the branch name
 */
public record BranchResponse(UUID id, UUID repoId, String name) {
    /**
     * Creates a {@link BranchResponse} from the given {@linkplain Branch}.
     *
     * @param branch a {@link Branch} object
     * @return the {@link BranchResponse} created from the given {@link Branch}
     */
    public static BranchResponse from(Branch branch) {
        return new BranchResponse(branch.getId(), branch.getRepo().getId(), branch.getName());
    }
}
