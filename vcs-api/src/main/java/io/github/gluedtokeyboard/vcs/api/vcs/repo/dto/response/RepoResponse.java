package io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.response;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.model.NodePath;

import java.util.UUID;

/**
 * Response DTO for repositories.
 *
 * @param id the repository ID
 * @param repoRoot the repository root along with its ID
 */
public record RepoResponse(UUID id, NodePath repoRoot) {
}
