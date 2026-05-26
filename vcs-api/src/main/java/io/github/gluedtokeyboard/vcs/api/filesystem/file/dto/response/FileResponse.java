package io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.response;

import java.util.UUID;

/**
 * The response DTO for file data.
 *
 * @param id the file ID
 * @param path the file path
 * @param contents the file contents
 */
public record FileResponse(UUID id, String path, String contents) {
}
