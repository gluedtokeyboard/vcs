package io.github.gluedtokeyboard.vcs.storage.filesystem;

import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.storage.StorageContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

@DisplayName("Filesystem Storage Test")
public class FileSystemStorageTest extends StorageContractTest {
    @TempDir
    Path tempDir;

    @Override
    protected Storage createStorage() {
        return new FileSystemStorage(tempDir.toString());
    }
}
