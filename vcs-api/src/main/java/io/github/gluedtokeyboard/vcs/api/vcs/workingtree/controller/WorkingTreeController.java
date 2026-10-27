package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.workingtree.dto.request.CheckoutFileRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.workingtree.dto.request.ResetRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.workingtree.service.WorkingTreeApplicationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Working Tree")
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
    public void reset(
            @Parameter(description = "The repository ID.")
            @PathVariable("repo_id")
            UUID repoId,

            @RequestBody ResetRequest resetRequest
    ) {
        workingTreeApplicationService.reset(repoId, resetRequest.commitPrefix());
    }

    /**
     * Checks out the given file from the given commit in the given repository.
     *
     * @param repoId the repository ID
     * @param checkoutFileRequest the checkout file request DTO containing a prefix of the commit's hash and the file
     *                            path
     */
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkoutFile(
            @Parameter(description = "The repository ID.")
            @PathVariable("repo_id")
            UUID repoId,

            @RequestBody CheckoutFileRequest checkoutFileRequest
    ) {
        workingTreeApplicationService.checkoutFile(
                repoId, checkoutFileRequest.commitPrefix(), checkoutFileRequest.path());
    }
}
