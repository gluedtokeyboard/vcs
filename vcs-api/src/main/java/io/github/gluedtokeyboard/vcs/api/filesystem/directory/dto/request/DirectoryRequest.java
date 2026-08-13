package io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.request;

import java.util.UUID;

/**
 * The request DTO for directory data.
 *
 * @param id the directory ID
 * @param path the directory path
 * @param copyFrom if present, the new directory would be a copy of this directory
 */
public record DirectoryRequest(UUID id, String path, UUID copyFrom) {
}
