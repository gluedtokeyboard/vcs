package io.github.gluedtokeyboard.vcs.api.vcs.commit.dto.request;

import io.github.gluedtokeyboard.vcs.api.vcs.commit.model.CommitType;

import java.util.UUID;

/**
 * Request DTO for creating commits.
 *
 * @param type the commit type
 * @param message the commit message in case of a normal commit
 * @param branchId the branch ID in case of a merge commit
 */
public record CommitRequest(CommitType type, String message, UUID branchId) {
}
