package io.github.gluedtokeyboard.vcs.api.vcs.staging.dto.response;

import io.github.gluedtokeyboard.vcs.api.vcs.staging.model.EntryType;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;

import java.util.UUID;

/**
 * Response DTO for the staging area.
 *
 * @param id the ID of the staging area entry
 * @param repoId the repository ID
 * @param relativePath the repository relative path of the entry
 * @param entryType the entry type
 */
public record StagingResponse(UUID id, UUID repoId, String relativePath, EntryType entryType) {
    /**
     * Creates a {@link StagingResponse} from the given {@link StagingEntry}.
     *
     * @param stagingEntry a {@link StagingEntry}
     * @return the {@link StagingResponse} created from the given {@link StagingEntry}
     */
    public static StagingResponse from(StagingEntry stagingEntry) {
        return new StagingResponse(
                stagingEntry.getId(), stagingEntry.getRepo().getId(), stagingEntry.getRelativePath(),
                stagingEntry.getType());
    }
}
