package io.github.gluedtokeyboard.vcs.api.vcs.repo.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.request.RepoRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.response.RepoResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.response.StatusResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.model.IdStatusRecord;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for managing repositories.
 */
@RestController
@RequestMapping("/api/v1/vcs/repos")
@RequiredArgsConstructor
public class RepoController {
    private final RepoApplicationService repoApplicationService;

    /**
     * Initializes a repository at the given root.
     *
     * @param repoRequest the repository DTO containing the repository root ID
     * @return the newly created repository
     */
    @PostMapping
    public ResponseEntity<RepoResponse> init(@RequestBody RepoRequest repoRequest) {
        UUID repoRootId = repoRequest.repoRootId();
        Repo repo = repoApplicationService.init(repoRootId);
        RepoResponse repoResponse = RepoResponse.from(repo);
        URI location = URI.create("/api/v1/vcs/repos/" + repo.getId());
        return ResponseEntity
                .created(location)
                .body(repoResponse);
    }

    /**
     * Searches for a repository upwards from the given directory.
     *
     * @param directoryId the directory ID
     * @return the repository found
     */
    @GetMapping
    public ResponseEntity<RepoResponse> resolveRepo(@RequestParam("directoryId") UUID directoryId) {
        Repo repo = repoApplicationService.resolveRepo(directoryId);
        return ResponseEntity.ok(RepoResponse.from(repo));
    }

    /**
     * Returns the status of the given repository.
     *
     * @param repoId the repository ID
     * @return the status of the given repository
     */
    @GetMapping("{repo_id}/status")
    public ResponseEntity<StatusResponse> status(@PathVariable("repo_id") UUID repoId) {
        IdStatusRecord statusRecord = repoApplicationService.status(repoId);
        return ResponseEntity.ok(StatusResponse.from(statusRecord));
    }
}
