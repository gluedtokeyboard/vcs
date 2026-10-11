package io.github.gluedtokeyboard.vcs.api.filesystem.common.config;

import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.storage.filesystem.FileSystemStorage;
import io.github.gluedtokeyboard.vcs.storage.inmemory.InMemoryStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    @Bean
    @ConditionalOnProperty(
            prefix = "storage",
            name = "type",
            havingValue = "inmemory"
    )
    public Storage inMemoryStorage() {
        return new InMemoryStorage();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "storage",
            name = "type",
            havingValue = "filesystem"
    )
    public Storage filesystemStorage(StorageProperties storageProperties) {
        return new FileSystemStorage(storageProperties.filesystem().root());
    }
}
