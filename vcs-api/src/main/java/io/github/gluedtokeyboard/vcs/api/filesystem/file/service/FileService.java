package io.github.gluedtokeyboard.vcs.api.filesystem.file.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
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
}
