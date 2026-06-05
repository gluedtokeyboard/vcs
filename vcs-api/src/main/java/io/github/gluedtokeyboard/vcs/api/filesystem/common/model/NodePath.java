package io.github.gluedtokeyboard.vcs.api.filesystem.common.model;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.storage.EntryType;

import java.util.UUID;

/**
 * Holds the path of a node along with its ID and type.
 *
 * @param id the path ID
 * @param path the node path
 * @param type the node type
 */
public record NodePath(UUID id, String path, EntryType type) {
    /**
     * Creates a {@link NodePath} from a {@link FilesystemNode} and an {@link EntryType}.
     *
     * @param node a {@link FilesystemNode}
     * @param type the node type
     * @return the {@link NodePath} created from the given {@link FilesystemNode}
     */
    public static NodePath from(FilesystemNode node, EntryType type) {
        return new NodePath(node.getId(), node.getPath(), type);
    }
}
