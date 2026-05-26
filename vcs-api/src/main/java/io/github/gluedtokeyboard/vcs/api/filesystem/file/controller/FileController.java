package io.github.gluedtokeyboard.vcs.api.filesystem.file.controller;

import io.github.gluedtokeyboard.vcs.api.filesystem.file.dto.response.FileResponse;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.model.FileContentResult;
import io.github.gluedtokeyboard.vcs.api.filesystem.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
