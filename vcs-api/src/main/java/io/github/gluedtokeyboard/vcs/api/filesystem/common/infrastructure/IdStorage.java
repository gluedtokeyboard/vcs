package io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.repository.FilesystemNodeRepository;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.storage.Entry;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.gluedtokeyboard.vcs.util.General.splitPath;

/**
 * Augments a {@linkplain Storage} with ID support.
 */
@Component
public class IdStorage implements Storage {
    /**
     * The object used for {@linkplain Storage} operations.
     */
    private final Storage storage;

    /**
     * Contains node service methods.
     */
    private final NodeService nodeService;

    /**
     * The object used to interact with the database.
     */
    private final FilesystemNodeRepository nodeRepository;

    /**
     * Stores the {@linkplain Storage} to augment and the node service and repository objects. Also creates a database
     * entry for the root node.
     *
     * @param storage the {@linkplain Storage} to augment
     * @param nodeService the object containing node service methods
     * @param nodeRepository the object used to interact with the database
     */
    public IdStorage(Storage storage, NodeService nodeService, FilesystemNodeRepository nodeRepository) {
        this.storage = storage;
        this.nodeService = nodeService;
        this.nodeRepository = nodeRepository;
        createRootNode();
    }

    /**
     * Creates a database entry for the root node only if it already does not exist.
     */
    private void createRootNode() {
        String root = "/";
        if (!nodeRepository.existsByPath(root)) {
            nodeService.createNode("/");
        }
    }

    /**
     * Reads the given file into a byte array.
     *
     * @param file the path of the file.
     * @return the contents of the file as a byte array.
     */
    @Override
    public byte[] read(String file) {
        return storage.read(file);
    }

    /**
     * Writes the given bytes to a file.
     *
     * @param contents the contents of the file.
     * @param file     the destination path.
     */
    @Override
    public void write(byte[] contents, String file) {
        boolean exists = storage.exists(file);
        storage.write(contents, file);
        if (!exists) {
            createNodes(file);
        }
    }

    /**
     * Checks if the given path exists.
     *
     * @param path the path of a folder or a file.
     * @return a boolean indicating whether the path exists.
     */
    @Override
    public boolean exists(String path) {
        return storage.exists(path);
    }

    /**
     * Creates the given folder along with the required intermediate folders.
     *
     * @param folder the path of the folder.
     */
    @Override
    public void mkdir(String folder) {
        storage.mkdir(folder);
        createNodes(folder);
    }

    /**
     * Deletes the given path.
     *
     * @param path the path to delete.
     */
    @Override
    public void rm(String path) {
        // Store the paths first (we are not deleting database entries right away in case rm throws an exception).
        List<String> paths = new ArrayList<>();
        walk(path, (_) -> false, true, true, false, paths::add);
        storage.rm(path);
        // If rm is successful, delete the database entries.
        paths.forEach(nodeService::deleteNode);
    }

    /**
     * Lists the contents of the given directory.
     *
     * @param folder the path of the directory.
     * @return a {@link List} containing the contents of the given directory as {@link Entry} objects.
     */
    @Override
    public List<Entry> ls(String folder) {
        return storage.ls(folder);
    }

    /**
     * Moves the given source to the given destination.
     *
     * @param source      the source path.
     * @param destination the destination path.
     */
    @Transactional
    @Override
    public void mv(String source, String destination) {
        storage.mv(source, destination);
        walk(destination, (_) -> false, true, true, false, (path) -> {
            String relativePath = getRelativePath(destination, path);
            String oldPath = join(source, relativePath);
            FilesystemNode node = nodeRepository.findByPath(oldPath).orElseThrow(IllegalArgumentException::new);
            node.setPath(path);
        });
    }

    /**
     * Copies the given source to the given destination.
     *
     * @param source      the source path
     * @param destination the destination path
     */
    @Override
    public void cp(String source, String destination) {
        storage.cp(source, destination);
        walk(destination, (_) -> false, true, true, false, path -> {
            FilesystemNode node = new FilesystemNode(path);
            nodeRepository.save(node);
        });
    }

    /**
     * Returns the basename of the given path.
     *
     * @param path a path.
     * @return the basename of the given path.
     */
    @Override
    public String basename(String path) {
        return storage.basename(path);
    }

    /**
     * Walks the given directory and execute the given {@link Consumer} on files and directories.
     *
     * @param directory            the directory to walk.
     * @param excludeDirs          a {@link Predicate} that returns {@code True} for the directories to skip.
     * @param consumeFiles         indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirsPreVisit  indicates whether to pass directories to the given {@link Consumer} before visiting
     *                             them.
     * @param consumeDirsPostVisit indicates whether to pass directories to the given {@link Consumer} after visiting
     *                             them.
     * @param consumer             a {@link Consumer} to consume the files and directories.
     */
    @Override
    public void walk(
            String directory, Predicate<String> excludeDirs, boolean consumeFiles, boolean consumeDirsPreVisit,
            boolean consumeDirsPostVisit, Consumer<String> consumer) {
        storage.walk(directory, excludeDirs, consumeFiles, consumeDirsPreVisit, consumeDirsPostVisit, consumer);
    }

    /**
     * Returns the directory of the given path.
     *
     * @param path a path.
     * @return the directory of the given path, or {@code null} if the given path does not have a parent.
     */
    @Override
    public String getDirectory(String path) {
        return storage.getDirectory(path);
    }

    /**
     * Returns the given path relative to the given base path.
     *
     * @param base the base path.
     * @param path a path.
     * @return the given path relative to the given base path.
     */
    @Override
    public String getRelativePath(String base, String path) {
        return storage.getRelativePath(base, path);
    }

    /**
     * Joins the given paths.
     *
     * @param base  the base path.
     * @param paths the paths to join to the base.
     * @return the joined path.
     */
    @Override
    public String join(String base, String... paths) {
        return storage.join(base, paths);
    }

    /**
     * Creates nodes for the given path and its intermediate directories if needed.
     *
     * @param path a path
     */
    private void createNodes(String path) {
        String separator = "/";
        List<String> parts = splitPath(path, separator);
        String intermediateDirectory = separator;
        for (String part : parts) {
            intermediateDirectory = join(intermediateDirectory, part);
            if (!nodeRepository.existsByPath(intermediateDirectory)) {
                nodeService.createNode(intermediateDirectory);
            }
        }
    }
}
