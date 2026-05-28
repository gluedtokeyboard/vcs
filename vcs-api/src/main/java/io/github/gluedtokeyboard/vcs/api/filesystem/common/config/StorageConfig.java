package io.github.gluedtokeyboard.vcs.api.filesystem.common.config;

import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.storage.inmemory.InMemoryStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    @Bean
    public Storage inMemoryStorage() {
        return new InMemoryStorage();
    }
}
