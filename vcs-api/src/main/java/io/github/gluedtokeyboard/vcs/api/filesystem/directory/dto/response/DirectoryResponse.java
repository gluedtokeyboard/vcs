package io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.response;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.model.NodeResult;

import java.util.List;
import java.util.UUID;

/**
 * The response DTO for directory data.
 *
 * @param id the directory ID
 * @param path the directory path
 * @param contents the directory contents
 */
public record DirectoryResponse(UUID id, String path, List<NodeResult> contents) {
}
