package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.dto.request;

/**
 * Request DTO for reset
 *
 * @param commitPrefix a prefix of the commit's hash to use for the reset
 */
public record ResetRequest(String commitPrefix) {
}
