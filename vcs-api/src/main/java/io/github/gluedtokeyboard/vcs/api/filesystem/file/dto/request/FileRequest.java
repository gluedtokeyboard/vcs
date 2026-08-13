package io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.request;

import java.util.UUID;

/**
 * The request DTO for file data.
 *
 * @param path the file path
 * @param contents the file contents
 * @param copyFrom if present, the new file would be a copy of this file
 */
public record FileRequest(String path, String contents, UUID copyFrom) {
}
