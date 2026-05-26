package io.github.gluedtokeyboard.vcs.api.filesystem.file.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.repository.FilesystemNodeRepository;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.model.FileContentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Contains service methods for files.
 */
@Service
@RequiredArgsConstructor
public class FileService {
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
     * Returns the file corresponding to the given ID.
     *
     * @param id the file ID.
     * @return the file corresponding to the given ID.
     */
    public FileContentResult getFile(UUID id) {
        String path = nodeService.getPath(id);
        String contents = storage.readString(path);
        return new FileContentResult(id, path, contents);
    }

    /**
     * Creates a new file.
     *
     * @param path the file path
     * @param contents the file contents
     * @return the newly created file
     * @throws IllegalArgumentException if a file already exists at the given path
     */
    public FileContentResult createFile(String path, String contents) {
        if (nodeRepository.existsByPath(path)) {
            throw new IllegalArgumentException("A file already exists at the given path");
        }
        storage.write(contents, path);
        UUID id = nodeService.getId(path);
        return new FileContentResult(id, path, contents);
    }

    /**
     * Updates the contents of the file.
     *
     * @param id the file ID
     * @param contents the new contents
     * @return the updated file
     */
    public FileContentResult updateContents(UUID id, String contents) {
        String path = nodeService.getPath(id);
        storage.write(contents, path);
        return new FileContentResult(id, path, contents);
    }

    /**
     * Updates the path of the file
     *
     * @param id the file ID
     * @param path the new path
     * @return the updated file
     */
    public FileContentResult updatePath(UUID id, String path) {
        String oldPath = nodeService.getPath(id);
        storage.mv(oldPath, path);
        String contents = storage.readString(path);
        return new FileContentResult(id, path, contents);
    }

    /**
     * Deletes the file corresponding to the given ID
     *
     * @param id the file ID
     */
    public void deleteFile(UUID id) {
        String path = nodeService.getPath(id);
        storage.rm(path);
    }
}
