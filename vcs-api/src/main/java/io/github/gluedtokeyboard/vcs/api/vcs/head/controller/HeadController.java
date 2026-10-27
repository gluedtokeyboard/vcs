package io.github.gluedtokeyboard.vcs.api.vcs.head.controller;

import io.github.gluedtokeyboard.vcs.api.vcs.head.dto.request.CheckoutBranchRequest;
import io.github.gluedtokeyboard.vcs.api.vcs.head.service.HeadApplicationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for head methods.
 */
@RestController
@RequestMapping("/api/v1/vcs/repos/{repo_id}/head")
@RequiredArgsConstructor
@Tag(name = "HEAD")
public class HeadController {
    private final HeadApplicationService headApplicationService;

    /**
     * Checks out the given branch in the given repository.
     *
     * @param repoId the repository ID
     * @param checkoutBranchRequest the checkout branch request DTO containing the branch ID
     */
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkoutBranch(
            @Parameter(description = "The repository ID.")
            @PathVariable("repo_id")
            UUID repoId,

            @RequestBody CheckoutBranchRequest checkoutBranchRequest
    ) {
        headApplicationService.checkoutBranch(repoId, checkoutBranchRequest.branchId());
    }
}
