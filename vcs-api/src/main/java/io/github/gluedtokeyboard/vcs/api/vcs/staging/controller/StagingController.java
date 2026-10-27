package io.github.gluedtokeyboard.vcs.api.vcs.staging.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.staging.dto.request.StagingRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.dto.response.StagingResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.service.StagingApplicationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for the staging area.
 */
@RestController
@RequestMapping("/api/v1/vcs/repos/{repo_id}/staging")
@RequiredArgsConstructor
@Tag(name = "Staging Area")
public class StagingController {
    private final StagingApplicationService stagingApplicationService;

    /**
     * Stages the given file in the given repository.
     *
     * @param repoID the repository ID
     * @param stagingRequest the request DTO containing the file to stage and the staging entry type
     * @return the newly created staging area entry
     */
    @PostMapping
    public ResponseEntity<StagingResponse> stageFile(
            @Parameter(description = "The repository ID.")
            @PathVariable("repo_id")
            UUID repoID,

            @RequestBody StagingRequest stagingRequest
    ) {
        StagingEntry stagingEntry = stagingApplicationService.stageFile(
                repoID, stagingRequest.fileId(), stagingRequest.entryType());
        if (stagingEntry == null) {
            // This happens when `rm` is executed on an untracked staged file, which simply unstages the file, resulting
            // in the missing staging entry.
            return ResponseEntity.noContent().build();
        }
        URI location = URI.create("/api/v1/vcs/repos/" + repoID + "/staging/" + stagingEntry.getId());
        return ResponseEntity
                .created(location)
                .body(StagingResponse.from(stagingEntry));
    }
}
