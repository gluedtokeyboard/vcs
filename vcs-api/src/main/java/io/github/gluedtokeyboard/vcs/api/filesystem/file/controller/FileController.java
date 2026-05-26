package io.github.gluedtokeyboard.vcs.api.filesystem.file.controller;

import io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.request.FileRequest;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.response.FileResponse;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.model.FileContentResult;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for file endpoints.
 */
@RestController
@RequestMapping("/api/v1/fs/files")
@RequiredArgsConstructor
public class FileController {
    /**
     * Used for service methods.
     */
    private final FileService fileService;

    /**
     * Returns the file corresponding to the given ID.
     *
     * @param id the file ID
     * @return the corresponding file
     */
    @GetMapping("{id}")
    public ResponseEntity<FileResponse> getFile(@PathVariable("id") UUID id) {
        FileContentResult result = fileService.getFile(id);
        FileResponse response = new FileResponse(result.id(), result.path(), result.contents());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new file.
     *
     * @param file the file DTO containing the path and the contents
     * @return the newly created file
     */
    @PostMapping
    public ResponseEntity<FileResponse> createFile(@RequestBody FileRequest file) {
        FileContentResult result = fileService.createFile(file.path(), file.contents());
        FileResponse response = new FileResponse(result.id(), result.path(), result.contents());
        URI location = URI.create("/api/v1/fs/files/" + result.id());
        return ResponseEntity
                .created(location)
                .body(response);
    }

    /**
     * Updates the file corresponding to the given ID.
     *
     * @param file the file DTO containing the new path and/or the new contents
     * @return the updated file
     */
    @PatchMapping("{id}")
    public ResponseEntity<FileResponse> updateFile(@PathVariable("id") UUID id, @RequestBody FileRequest file) {
        FileContentResult result = null;
        if (file.path() != null) {
            result = fileService.updatePath(id, file.path());
        }
        if (file.contents() != null) {
            result = fileService.updateContents(id, file.contents());
        }
        return ResponseEntity.ok(new FileResponse(result.id(), result.path(), result.contents()));
    }

    /**
     * Deletes the file corresponding to the given ID.
     *
     * @param id the file ID
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable("id") UUID id) {
        fileService.deleteFile(id);
    }
}
