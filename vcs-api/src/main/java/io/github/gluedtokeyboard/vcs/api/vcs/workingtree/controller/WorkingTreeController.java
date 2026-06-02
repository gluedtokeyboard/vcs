package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.workingtree.dto.request.ResetRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.workingtree.service.WorkingTreeApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for working tree methods.
 */
@RestController
@RequestMapping("/api/v1/vcs/repos/{repo_id}/working-tree")
@RequiredArgsConstructor
public class WorkingTreeController {
    private final WorkingTreeApplicationService workingTreeApplicationService;

    /**
     * Resets the given repository to the given commit.
     *
     * @param repoId the repository ID
     * @param resetRequest the reset request DTO containing a prefix of the commit's hash
     */
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(@PathVariable("repo_id") UUID repoId, @RequestBody ResetRequest resetRequest) {
        workingTreeApplicationService.reset(repoId, resetRequest.commitPrefix());
    }
}
