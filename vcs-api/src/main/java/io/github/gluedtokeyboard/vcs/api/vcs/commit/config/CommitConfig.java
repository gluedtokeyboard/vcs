package io.github.gluedtokeyboard.vcs.api.vcs.commit.config;

import io.github.gluedtokeyboard.vcs.api.vcs.infrastructure.context.InfrastructureContext;
import io.github.gluedtokeyboard.vcs.core.service.command.*;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommitConfig {
    private final InfrastructureContext ctx;

    @Bean
    public CommitService commitService(GeneralService generalService) {
        return new CommitService(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), generalService);
    }

    @Bean
    public CommitCommand commitCommand(CommitService commitService) {
        return new CommitCommand(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), commitService);
    }

    @Bean
    public Log log() {
        return new Log(ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore());
    }

    @Bean
    public Merge merge(
            Add add, Rm rm, CommitCommand commitCommand, GeneralService generalService, CommitService commitService,
            WorkingTreeService workingTreeService) {
        return new Merge(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), add, rm,
                commitCommand, generalService, commitService, workingTreeService);
    }
}
