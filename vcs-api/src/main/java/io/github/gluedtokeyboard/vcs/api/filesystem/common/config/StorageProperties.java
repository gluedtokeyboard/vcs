package io.github.gluedtokeyboard.vcs.api.filesystem.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
        String type,
        Filesystem filesystem
) {
    public record Filesystem(
            String root
    ) {}
}
