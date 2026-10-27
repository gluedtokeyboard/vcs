package io.github.gluedtokeyboard.vcs.api.filesystem.directory.controller;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.request.DirectoryRequest;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.dto.response.DirectoryResponse;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.service.DirectoryService;
import io.github.gluedtokeyboard.vcs.api.filesystem.directory.service.model.DirectoryContentResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Directory")
public class DirectoryController {
    /**
     * Used for service methods.
     */
    private final DirectoryService directoryService;

    private final NodeService nodeService;

    /**
     * Returns the directory corresponding to the given ID.
     *
     * @param id the directory ID
     * @return the corresponding directory
     */
    @GetMapping("{id}")
    public ResponseEntity<DirectoryResponse> getDirectory(
            @Parameter(description = "The directory ID.")
            @PathVariable("id")
            UUID id
    ) {
        DirectoryContentResult result = directoryService.getDirectory(id);
        DirectoryResponse response = new DirectoryResponse(result.id(), result.path(), result.contents());
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the root directory.
     *
     * @return the root directory
     */
    @GetMapping("root")
    public ResponseEntity<DirectoryResponse> getRoot() {
        String root = "/";
        UUID rootId = nodeService.getId(root);
        return getDirectory(rootId);
    }

    /**
     * Creates a new directory, or creates a copy of an existing directory.
     *
     * @param directory the directory DTO containing its path and optionally a source ID in case of creating a copy
     * @return the newly created directory
     */
    @PostMapping
    public ResponseEntity<DirectoryResponse> createDirectory(@RequestBody DirectoryRequest directory) {
        DirectoryContentResult result;
        UUID copyFrom = directory.copyFrom();
        if (copyFrom != null) {
            result = directoryService.copyDirectory(copyFrom, directory.path());
        } else {
            result = directoryService.createDirectory(directory.path());
        }
        DirectoryResponse response = new DirectoryResponse(result.id(), result.path(), result.contents());
        URI location = URI.create("/api/v1/fs/directories/" + result.id());
        return ResponseEntity
                .created(location)
                .body(response);
    }

    /**
     * Deletes the directory corresponding to the given ID.
     *
     * @param id the directory ID
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirectory(
            @Parameter(description = "The directory ID.")
            @PathVariable("id")
            UUID id
    ) {
        directoryService.deleteDirectory(id);
    }

    /**
     * Updates the path of the directory corresponding to the given ID.
     *
     * @param directory the directory DTO containing the new path
     * @return the updated directory
     */
    @PatchMapping("{id}")
    public ResponseEntity<DirectoryResponse> updateDirectoryPath(
            @Parameter(description = "The directory ID.")
            @PathVariable("id")
            UUID id,

            @RequestBody DirectoryRequest directory
) {
        DirectoryContentResult result = directoryService.updateDirectoryPath(id, directory.path());
        return ResponseEntity.ok(new DirectoryResponse(result.id(), result.path(), result.contents()));
    }
}
