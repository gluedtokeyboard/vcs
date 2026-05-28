package io.github.gluedtokeyboard.vcs.api.vcs.head.config;

import io.github.gluedtokeyboard.vcs.api.vcs.infrastructure.context.InfrastructureContext;
import io.github.gluedtokeyboard.vcs.core.service.command.Checkout;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HeadConfig {
    private final InfrastructureContext ctx;

    @Bean
    public Checkout checkout(GeneralService generalService, WorkingTreeService workingTreeService) {
        return new Checkout(
                ctx.storage(), ctx.vcsLayout(), ctx.vcsStore(), ctx.blobStore(), ctx.commitStore(), generalService,
                workingTreeService);
    }
}
