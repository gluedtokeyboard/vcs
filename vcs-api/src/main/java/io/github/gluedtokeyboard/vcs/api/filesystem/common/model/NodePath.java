package io.github.gluedtokeyboard.vcs.api.filesystem.common.model;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;

import java.util.UUID;

/**
 * Holds the path of a node along with its ID.
 *
 * @param id the path ID
 * @param path the node path
 */
public record NodePath(UUID id, String path) {
    /**
     * Creates a {@link NodePath} from a {@link FilesystemNode}.
     *
     * @param node a {@link FilesystemNode}
     * @return the {@link NodePath} created from the given {@link FilesystemNode}
     */
    public static NodePath from(FilesystemNode node) {
        return new NodePath(node.getId(), node.getPath());
    }
}
