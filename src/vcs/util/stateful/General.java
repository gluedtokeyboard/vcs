package vcs.util.stateful;

import vcs.core.VCS;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import static vcs.util.stateless.FileSystem.getCWD;
import static vcs.util.stateless.FileSystem.walk;

/**
 * General stateful utils.
 */
public final class General extends StatefulUtil {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public General(VCS vcs) {
        super(vcs);
    }

    /**
     * Walks the repository root.
     * @param consumeFiles indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirsPreVisit indicates whether to pass directories to the given {@link Consumer} before visiting
     *                           them.
     * @param consumeDirsPostVisit indicates whether to pass directories to the given {@link Consumer} after visiting
     *                            them.
     * @param consumer a {@link Consumer} to consume the repository files and directories.
     */
    public static void walkRepo(
            boolean consumeFiles, boolean consumeDirsPreVisit, boolean consumeDirsPostVisit, Consumer<Path> consumer) {
        walk(getCWD(), Set.of(VCS.getRepoFolder()), consumeFiles, consumeDirsPreVisit, consumeDirsPostVisit, consumer);
    }

    /**
     * Walks the repository root, but only feeds files to the consumer.
     * @param consumer a {@link Consumer} to consume the repository files.
     */
    public static void walkRepoFiles(Consumer<Path> consumer) {
        walkRepo(true, false, false, consumer);
    }

    /**
     * Walks the repository root, but only feeds directories to the consumer before visiting them.
     * @param consumer a {@link Consumer} to consume all the directories in the repository.
     */
    public static void walkRepoDirsPre(Consumer<Path> consumer) {
        walkRepo(false, true, false, consumer);
    }

    /**
     * Walks the repository root, but only feeds directories to the consumer after visiting them.
     * @param consumer a {@link Consumer} to consume all the directories in the repository.
     */
    public static void walkRepoDirsPost(Consumer<Path> consumer) {
        walkRepo(false, false, true, consumer);
    }
}
