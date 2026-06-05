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
public record NodeResult(UUID id, String path, EntryType type) {
    /**
     * Creates a {@link NodeResult} from a {@link FilesystemNode} and an {@link EntryType}.
     *
     * @param node a {@link FilesystemNode}
     * @param type the node type
     * @return the {@link NodeResult} created from the given {@link FilesystemNode}
     */
    public static NodeResult from(FilesystemNode node, EntryType type) {
        return new NodeResult(node.getId(), node.getPath(), type);
    }
}
