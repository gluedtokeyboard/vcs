package io.github.gluedtokeyboard.vcs.api.vcs.workingtree.config;

import io.github.gluedtokeyboard.vcs.api.vcs.infrastructure.context.InfrastructureContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Reset;
import io.github.gluedtokeyboard.vcs.core.service.command.Status;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WorkingTreeConfig {
    private final InfrastructureContext ctx;

    @Bean
    public Reset reset(WorkingTreeService workingTreeService) {
        return new Reset(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), workingTreeService);
    }

    @Bean
    public WorkingTreeService workingTreeService(
            InfrastructureContext ctx, Status status, GeneralService generalService, CommitService commitService) {
        return new WorkingTreeService(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), status,
                generalService, commitService);
    }
}
