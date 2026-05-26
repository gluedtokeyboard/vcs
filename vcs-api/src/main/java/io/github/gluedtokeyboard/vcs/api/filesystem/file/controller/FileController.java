package io.github.gluedtokeyboard.vcs.api.filesystem.file.controller;

import io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.request.FileRequest;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.response.FileResponse;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.model.FileContentResult;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.FileService;
import lombok.RequiredArgsConstructor;
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
}
