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
}
