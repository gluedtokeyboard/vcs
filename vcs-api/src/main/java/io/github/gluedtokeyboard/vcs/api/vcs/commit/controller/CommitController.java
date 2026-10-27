package io.github.gluedtokeyboard.vcs.api.vcs.commit.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.commit.dto.request.CommitRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.commit.dto.response.CommitResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.commit.model.CommitType;
import io.github.gluedtokeyboard.vcs.api.vcs.commit.service.CommitApplicationService;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Controller for commits.
 */
@RestController
@RequestMapping("/api/v1/vcs/repos/{repo_id}/commits")
@RequiredArgsConstructor
@Tag(name = "Commit")
public class CommitController {
    private final CommitApplicationService commitService;

    /**
     * Creates a commit in the given repository.
     *
     * @param repoId the repository ID
     * @param commitRequest the commit request DTO containing the commit type, the commit message in case of a normal
     *                      commit, or the branch ID in case of a merge commit
     * @return the created commit
     */
    @PostMapping
    public ResponseEntity<CommitResponse> commit(
            @Parameter(description = "The repository ID.")
            @PathVariable("repo_id")
            UUID repoId,

            @RequestBody CommitRequest commitRequest
    ) {
        CommitType type = commitRequest.type();
        Commit commit = commitService.commit(repoId, type, commitRequest.message(), commitRequest.branchId());
        URI location = URI.create("/api/v1/vcs/repos" + repoId + "/commits/" + commit.getSha1());
        return ResponseEntity
                .created(location)
                .body(CommitResponse.from(commit, repoId));
    }

    /**
     * Returns the commit log of the given repository.
     *
     * @param repoId the repository ID
     * @return the commit log of the given repository
     */
    @GetMapping
    public ResponseEntity<List<CommitResponse>> log(
            @Parameter(description = "The repository ID.")
            @PathVariable("repo_id")
            UUID repoId
    ) {
        List<Commit> commits = commitService.log(repoId);
        return ResponseEntity.ok(
                commits
                    .stream()
                    .map(commit -> CommitResponse.from(commit, repoId))
                    .toList()
        );
    }
}
