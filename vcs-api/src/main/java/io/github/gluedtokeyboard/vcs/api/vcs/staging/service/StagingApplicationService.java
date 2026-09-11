package io.github.gluedtokeyboard.vcs.api.vcs.staging.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext.RepoContext;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.model.EntryType;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.entity.StagingEntry;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Add;
import io.github.gluedtokeyboard.vcs.core.service.command.Rm;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Staging area service methods.
 */
@Service
@RequiredArgsConstructor
public class StagingApplicationService {
    private final RepoContext repoContext;
    private final NodeService nodeService;
    private final StagingService stagingService;
    private final Add add;
    private final Rm rm;
    private final GeneralService generalService;

    /**
     * Stages the given file in the given repository.
     *
     * @param repoId the repository ID
     * @param fileId the file ID
     * @param entryType the type of the staging entry
     * @return the newly created staging entry
     */
    @Transactional
    public StagingEntry stageFile(UUID repoId, UUID fileId, EntryType entryType) {
        VcsContext ctx = repoContext.getContextByRepoId(repoId);
        String path = nodeService.getPath(fileId);
        BiConsumer<VcsContext, String> stagingFunction = switch (entryType) {
            case EntryType.ADDITION -> add::add;
            case EntryType.DELETION -> rm::rm;
        };
        stagingFunction.accept(ctx, path);
        String repoRelativePath = generalService.getRepoRelativePath(ctx.repoRoot(), path);
        StagingEntry entry;
        try {
            entry = stagingService.getEntry(repoId, repoRelativePath);
        } catch (NoSuchElementException e) {
            // This happens when `rm` is executed on an untracked staged file, which simply unstages the file, resulting
            // in the missing staging entry.
            entry = null;
        }
        repoContext.storeVcs(ctx);
        return entry;
    }
}
