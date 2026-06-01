package io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.request;

import java.util.UUID;

/**
 * Request DTO for repositories.
 *
 * @param repoRootId the repository root ID
 */
public record RepoRequest(UUID repoRootId) {
}
