package io.github.gluedtokeyboard.vcs.storage.inmemory;

import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.storage.StorageContractTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("In-Memory Storage Test")
public class InMemoryStorageTest extends StorageContractTest {
    @Override
    protected Storage createStorage() {
        return new InMemoryStorage();
    }
}
