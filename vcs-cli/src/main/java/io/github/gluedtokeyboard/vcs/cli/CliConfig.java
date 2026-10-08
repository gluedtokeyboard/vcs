package io.github.gluedtokeyboard.vcs.cli;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.*;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.DefaultVcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.BlobStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.CommitStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.DefaultVcsStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.persistence.objectstore.DefaultObjectStore;
import io.github.gluedtokeyboard.vcs.persistence.objectstore.ObjectStore;
import io.github.gluedtokeyboard.vcs.persistence.serialization.JavaBinarySerializer;
import io.github.gluedtokeyboard.vcs.persistence.serialization.Serializer;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.storage.filesystem.FileSystemStorage;

import java.nio.file.Path;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.getCWD;

public class CliConfig {
    static Storage storage() {
        String root = Path.of(getCWD()).getRoot().toString();
        return new FileSystemStorage(root);
    }

    static VcsLayout vcsLayout() {
        return new DefaultVcsLayout(storage());
    }

    static VcsStore vcsStore() {
        return new DefaultVcsStore(objectStore(), vcsLayout(), storage());
    }

    static ObjectStore objectStore() {
        return new DefaultObjectStore(storage(), serializer());
    }

    static Serializer serializer() {
        return new JavaBinarySerializer();
    }

    static ContentAddressableStore<Blob> blobStore() {
        return new BlobStore(vcsLayout(), storage());
    }

    static ContentAddressableStore<Commit> commitStore() {
        return new CommitStore(objectStore(), vcsLayout(), storage());
    }

    static GeneralService generalService() {
        return new GeneralService(storage(), vcsLayout(), vcsStore(), blobStore(), commitStore());
    }

    static StagingAreaService stagingAreaService() {
        return new StagingAreaService(storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), generalService());
    }

    static CommitService commitService() {
        return new CommitService(storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), generalService());
    }

    static WorkingTreeService workingTreeService() {
        return new WorkingTreeService(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), status(), generalService(),
                commitService());
    }

    static Init init() {
        return new Init(storage(), vcsLayout(), vcsStore(), blobStore(), commitStore());
    }

    static Add add() {
        return new Add(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), generalService(), stagingAreaService(),
                commitService());
    }

    static CommitCommand commitCommand() {
        return new CommitCommand(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), commitService(), stagingAreaService());
    }

    static Rm rm() {
        return new Rm(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), generalService(), stagingAreaService());
    }

    static Log log() {
        return new Log(storage(), vcsLayout(), vcsStore(), blobStore(), commitStore());
    }

    static Status status() {
        return new Status(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), generalService(), stagingAreaService(),
                commitService());
    }

    static Branch branch() {
        return new Branch(storage(), vcsLayout(), vcsStore(), blobStore(), commitStore());
    }

    static Checkout checkout() {
        return new Checkout(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), generalService(), workingTreeService());
    }

    static Reset reset() {
        return new Reset(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), workingTreeService());
    }

    static Merge merge() {
        return new Merge(
                storage(), vcsLayout(), vcsStore(), blobStore(), commitStore(), add(), rm(), commitCommand(),
                generalService(), commitService(), workingTreeService());
    }
}
