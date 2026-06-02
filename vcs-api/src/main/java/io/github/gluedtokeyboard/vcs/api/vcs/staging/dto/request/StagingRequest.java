package io.github.gluedtokeyboard.vcs.api.vcs.staging.dto.request;

import io.github.gluedtokeyboard.vcs.api.vcs.staging.model.EntryType;

import java.util.UUID;

/**
 * Request DTO for the staging area.
 *
 * @param fileId the file ID
 * @param entryType the type of the staging entry
 */
public record StagingRequest(UUID fileId, EntryType entryType) {
}
