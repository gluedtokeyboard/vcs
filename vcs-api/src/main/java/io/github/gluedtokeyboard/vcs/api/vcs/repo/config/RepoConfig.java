package io.github.gluedtokeyboard.vcs.api.vcs.repo.config;

import io.github.gluedtokeyboard.vcs.api.vcs.infrastructure.context.InfrastructureContext;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.service.IdStagingAreaService;
import io.github.gluedtokeyboard.vcs.core.service.command.Status;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RepoConfig {
    private final InfrastructureContext ctx;

    @Bean
    public GeneralService generalService() {
        return new GeneralService(ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore());
    }

    @Bean
    public Status status(
            GeneralService generalService, IdStagingAreaService stagingAreaService, CommitService commitService) {
        return new Status(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), generalService,
                stagingAreaService, commitService);
    }
}
