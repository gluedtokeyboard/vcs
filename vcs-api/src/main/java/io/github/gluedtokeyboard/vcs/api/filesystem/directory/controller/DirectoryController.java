package io.github.gluedtokeyboard.vcs.api.filesystem.directory.controller;

import io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.request.DirectoryRequest;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.response.DirectoryResponse;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.service.DirectoryService;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.service.model.DirectoryContentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for directory endpoints.
 */
@RestController
@RequestMapping("/api/v1/fs/directories")
@RequiredArgsConstructor
public class DirectoryController {
    /**
     * Used for service methods.
     */
    private final DirectoryService directoryService;

    /**
     * Returns the directory corresponding to the given ID.
     *
     * @param id the directory ID
     * @return the corresponding directory
     */
    @GetMapping("{id}")
    public ResponseEntity<DirectoryResponse> getDirectory(@PathVariable("id") UUID id) {
        DirectoryContentResult result = directoryService.getDirectory(id);
        DirectoryResponse response = new DirectoryResponse(result.id(), result.path(), result.contents());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new directory.
     *
     * @param directory the directory DTO containing its path
     * @return the newly created directory
     */
    @PostMapping
    public ResponseEntity<DirectoryResponse> createDirectory(@RequestBody DirectoryRequest directory) {
        DirectoryContentResult result = directoryService.createDirectory(directory.path());
        DirectoryResponse response = new DirectoryResponse(result.id(), result.path(), result.contents());
        URI location = URI.create("/api/v1/fs/directories/" + result.id());
        return ResponseEntity
                .created(location)
                .body(response);
    }
}
