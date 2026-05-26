package io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.request;

import java.util.UUID;

/**
 * The request DTO for directory data.
 *
 * @param id the directory ID
 * @param path the directory path
 */
public record DirectoryRequest(UUID id, String path) {
}
