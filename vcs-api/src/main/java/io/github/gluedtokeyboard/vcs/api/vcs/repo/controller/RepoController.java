package io.github.gluedtokeyboard.vcs.api.vcs.repo.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.request.RepoRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.dto.response.RepoResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
