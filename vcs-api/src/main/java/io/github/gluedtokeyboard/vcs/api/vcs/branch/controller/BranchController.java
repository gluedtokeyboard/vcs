package io.github.gluedtokeyboard.vcs.api.vcs.branch.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.branch.dto.request.BranchRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.dto.response.BranchResponse;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.entity.Branch;
import io.github.gluedtokeyboard.vcs.api.vcs.branch.service.BranchApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for branches.
 */
@RestController
@RequestMapping("/api/v1/vcs/repos/{repo_id}/branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchApplicationService branchApplicationService;

    /**
     * Creates a new branch in the given repository.
     *
     * @param branchRequest the branch DTO containing the branch name
     * @return the newly created branch
     */
    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(
            @PathVariable("repo_id") UUID repoId, @RequestBody BranchRequest branchRequest) {
        Branch branch = branchApplicationService.createBranch(repoId, branchRequest.name());
        URI location = URI.create("/api/v1/vcs/repos/" + repoId + "/branches/" + branch.getId());
        return ResponseEntity
                .created(location)
                .body(BranchResponse.from(branch));
    }

    /**
     * Removes the given branch.
     *
     * @param id the branch ID
     */
    @DeleteMapping("{branch_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBranch(@PathVariable("branch_id") UUID id) {
        branchApplicationService.removeBranch(id);
    }
}
