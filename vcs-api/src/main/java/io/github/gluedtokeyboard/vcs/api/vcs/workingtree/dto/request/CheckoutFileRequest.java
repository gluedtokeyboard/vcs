package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.dto.request;

/**
 * Request DTO for checking out a file.
 *
 * @param commitPrefix a prefix of the commit's hash to use for the checkout
 * @param path the file path
 */
public record CheckoutFileRequest(String commitPrefix, String path) {
}
