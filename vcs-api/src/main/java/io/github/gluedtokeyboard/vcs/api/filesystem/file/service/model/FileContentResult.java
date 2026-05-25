package io.github.gluedtokeyboard.vcs.api.filesystem.file.service.model;

import java.util.UUID;

/**
 * Used as the return type for file service methods.
 *
 * @param id the file ID
 * @param path the file path
 * @param contents the file contents
 */
public record FileContentResult(UUID id, String path, String contents) {}
