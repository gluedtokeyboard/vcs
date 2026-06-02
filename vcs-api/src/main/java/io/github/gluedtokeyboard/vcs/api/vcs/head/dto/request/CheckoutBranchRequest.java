package io.github.gluedtokeyboard.vcs.api.vcs.head.dto.request;

import java.util.UUID;

/**
 * Request DTO for checking out branches.
 *
 * @param branchId the branch ID
 */
public record CheckoutBranchRequest(UUID branchId) {
}
