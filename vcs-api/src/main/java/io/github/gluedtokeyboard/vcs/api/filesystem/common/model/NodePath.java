package io.github.gluedtokeyboard.vcs.api.filesystem.common.model;

import java.util.UUID;

/**
 * Holds the path of a node along with its ID.
 *
 * @param id the path ID
 * @param path the node path
 */
public record NodePath(UUID id, String path) {
}
