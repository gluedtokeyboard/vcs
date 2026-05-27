package io.github.gluedtokeyboard.vcs.api.filesystem.common.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.repository.FilesystemNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service methods for nodes.
 */
@Service
@RequiredArgsConstructor
public class NodeService {
    /**
     * The object used to interact with the database.
     */
    private final FilesystemNodeRepository nodeRepository;

    /**
     * Creates a new node.
     *
     * @param path the node path
     * @return the newly created node
     */
    public FilesystemNode createNode(String path) {
        return nodeRepository.save(new FilesystemNode(path));
    }

    /**
     * Returns the {@link FilesystemNode} corresponding to the given ID.
     *
     * @param id the node ID
     * @return the corresponding {@link FilesystemNode}
     * @throws NoSuchElementException if a node with the given ID does not exist
     */
    public FilesystemNode getNode(UUID id) {
        return nodeRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the path of the node corresponding to the given ID.
     *
     * @param id the node ID
     * @return the path of the corresponding node
     */
    public String getPath(UUID id) {
        return getNode(id).getPath();
    }

    /**
     * Returns the ID of the node corresponding to the given path.
     *
     * @param path the node path
     * @return the corresponding ID
     */
    public UUID getId(String path) {
        return nodeRepository.findByPath(path).orElseThrow(NoSuchElementException::new).getId();
    }

    /**
     * Deletes the node corresponding to the given path.
     *
     * @param path the node path.
     */
    public void deleteNode(String path) {
        FilesystemNode node = nodeRepository.findByPath(path).orElseThrow(NoSuchElementException::new);
        nodeRepository.delete(node);
    }
}
