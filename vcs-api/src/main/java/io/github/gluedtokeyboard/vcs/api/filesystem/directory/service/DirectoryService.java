package io.github.gluedtokeyboard.vcs.api.filesystem.directory.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.model.NodePath;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.repository.FilesystemNodeRepository;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.service.model.DirectoryContentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service methods for directories.
 */
@Service
@RequiredArgsConstructor
public class DirectoryService {
    /**
     * The object used to interact with the storage.
     */
    private final IdStorage storage;

    /**
     * Contains node service methods.
     */
    private final NodeService nodeService;

    /**
     * The object used to interact with the database.
     */
    private final FilesystemNodeRepository nodeRepository;

    /**
     * Returns the directory corresponding to the given ID.
     *
     * @param id the directory ID
     * @return the directory corresponding to the given ID
     */
    public DirectoryContentResult getDirectory(UUID id) {
        String path = nodeService.getPath(id);
        List<NodePath> contents = storage.ls(path).stream()
                .map(entry -> {
                    String entryPath = entry.path();
                    UUID entryId = nodeRepository.findByPath(entryPath).get().getId();
                    return new NodePath(entryId, entryPath, entry.type());
                })
                .toList();
        return new DirectoryContentResult(id, path, contents);
    }

    /**
     * Creates a new directory.
     *
     * @param path the directory path
     * @return the newly created directory
     * @throws IllegalArgumentException if a directory already exists at the given path
     */
    public DirectoryContentResult createDirectory(String path) {
        if (nodeRepository.existsByPath(path)) {
            throw new IllegalArgumentException("A directory already exists at the given path");
        }
        storage.mkdir(path);
        UUID id = nodeService.getId(path);
        return new DirectoryContentResult(id, path, List.of());
    }
}
