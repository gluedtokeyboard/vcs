package io.github.gluedtokeyboard.vcs.api.vcs.staging.config;

import io.github.gluedtokeyboard.vcs.api.vcs.infrastructure.context.InfrastructureContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Add;
import io.github.gluedtokeyboard.vcs.core.service.command.Rm;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StagingConfig {
    private final InfrastructureContext ctx;

    @Bean
    public StagingAreaService stagingAreaService(GeneralService generalService) {
        return new StagingAreaService(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), generalService);
    }

    @Bean
    public Add add(GeneralService generalService, StagingAreaService stagingAreaService, CommitService commitService) {
        return new Add(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), generalService,
                stagingAreaService, commitService);
    }

    @Bean
    public Rm rm(GeneralService generalService, StagingAreaService stagingAreaService) {
        return new Rm(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), generalService,
                stagingAreaService);
    }
}
