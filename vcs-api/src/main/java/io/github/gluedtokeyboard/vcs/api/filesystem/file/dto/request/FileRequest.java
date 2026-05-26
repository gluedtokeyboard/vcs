package io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.request;

/**
 * The request DTO for file data.
 *
 * @param path the file path
 * @param contents the file contents
 */
public record FileRequest(String path, String contents) {
}
