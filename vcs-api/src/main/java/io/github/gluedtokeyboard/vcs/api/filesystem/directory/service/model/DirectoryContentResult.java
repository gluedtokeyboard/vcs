package io.github.gluedtokeyboard.vcs.api.filesystem.directory.service.model;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.model.NodeResult;

import java.util.List;
import java.util.UUID;

/**
 * Used as the return type for directory service methods.
 *
 * @param id the directory ID
 * @param path the directory path
 * @param contents the contents of the directory
 */
public record DirectoryContentResult(UUID id, String path, List<NodeResult> contents) {}
