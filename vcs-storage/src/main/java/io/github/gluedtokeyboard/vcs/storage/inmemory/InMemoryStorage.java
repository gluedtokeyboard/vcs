package io.github.gluedtokeyboard.vcs.storage.inmemory;

import io.github.gluedtokeyboard.vcs.storage.Entry;
import io.github.gluedtokeyboard.vcs.storage.EntryType;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.gluedtokeyboard.vcs.util.General.splitPath;

/**
 * An in-memory {@link Storage} implementation
 */
public class InMemoryStorage implements Storage {
    /**
     * The path separator.
     */
    private static final String SEPARATOR = "/";

    /**
     * The root directory.
     */
    private final DirectoryNode root = new DirectoryNode("", null);

    /**
     * The path of the root directory.
     */
    private static final String ROOT_PATH = SEPARATOR;

    /**
     * Reads the given file into a byte array.
     *
     * @param file the path of the file.
     * @return the contents of the file as a byte array.
     * @throws NoSuchElementException if the given file does not exist.
     * @throws IllegalArgumentException if the given path is not a file.
     */
    @Override
    public byte[] read(String file) {
        Node node = findNode(file);
        if (node == null) {
            throw new NoSuchElementException("File: " + file + " does not exist.");
        }
        if (!(node instanceof FileNode fileNode)) {
            throw new IllegalArgumentException(file + " is not a file.");
        }
        return Arrays.copyOf(fileNode.contents, fileNode.contents.length);
    }

    /**
     * Writes the given bytes to a file.
     *
     * @param contents the contents of the file.
     * @param file     the destination path.
     * @throws IllegalArgumentException if the given path is not a file.
     */
    @Override
    public void write(byte[] contents, String file) {
        byte[] newContents = Arrays.copyOf(contents, contents.length);
        Node node = findNode(file);
        if (node != null) {
            if (!(node instanceof FileNode fileNode)) {
                throw new IllegalArgumentException(file + " is not a file.");
            }
            fileNode.contents = newContents;
        } else {
            String directory = getDirectory(file);
            DirectoryNode parent = mkdirs(directory);
            String basename = basename(file);
            FileNode newNode = new FileNode(basename, parent, newContents);
            parent.children.put(newNode.name, newNode);
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
        return findNode(path) != null;
    }

    /**
     * Creates the given folder along with the required intermediate folders.
     *
     * @param folder the path of the folder.
     */
    @Override
    public void mkdir(String folder) {
        mkdirs(folder);
    }

    /**
     * Deletes the given file.
     *
     * @param path the path to delete.
     */
    @Override
    public void rm(String path) {
        Node node = findNode(path);
        if (node != null && node != root) {
            node.parent.children.remove(node.name);
        }
    }

    /**
     * Lists the contents of the given directory.
     *
     * @param folder the path of the directory.
     * @return a {@link List} containing the contents of the given directory as {@link Entry} objects.
     * @throws IllegalArgumentException if the given path is not a directory.
     * @throws NoSuchElementException if the folder does not exist.
     */
    @Override
    public List<Entry> ls(String folder) {
        Node node = findNode(folder);
        if (node == null) {
            throw new NoSuchElementException("Folder: " + folder + " does not exist.");
        }
        if (!(node instanceof DirectoryNode dir)) {
            throw new IllegalArgumentException(folder + " is not a folder.");
        }
        return dir.children.values().stream()
                .map(child -> {
                    EntryType type = child instanceof FileNode ? EntryType.FILE : EntryType.DIRECTORY;
                    String childPath = join(folder, child.name);
                    return new Entry(childPath, type);
                })
                .toList();
    }

    /**
     * Moves the given source to the given destination.
     *
     * @param source      the source path.
     * @param destination the destination path.
     * @throws NoSuchElementException if the source does not exist.
     * @throws IllegalArgumentException if the source is the root directory.
     */
    @Override
    public void mv(String source, String destination) {
        Node node = findNode(source);
        if (node == null) {
            throw new NoSuchElementException("Source " + source + " does not exist.");
        }
        if (node == root) {
            throw new IllegalArgumentException("Cannot move the root directory");
        }
        String newDirectoryPath = getDirectory(destination);
        DirectoryNode newDirectory = mkdirs(newDirectoryPath);
        String newName = basename(destination);
        node.parent.children.remove(node.name);
        node.parent = newDirectory;
        node.name = newName;
        newDirectory.children.put(newName, node);
    }

    /**
     * Copies the given source to the given destination.
     *
     * @param source the source path
     * @param destination the destination path
     */
    @Override
    public void cp(String source, String destination) {
        Node node = findNode(source);
        if (node == null) {
            throw new NoSuchElementException("Source " + source + " does not exist.");
        }
        String newName = basename(destination);
        node = node.copy();
        node.name = newName;
        String newDirectoryPath = getDirectory(destination);
        DirectoryNode newDirectory = mkdirs(newDirectoryPath);
        node.parent = newDirectory;
        newDirectory.children.put(newName, node);
    }

    /**
     * Returns the basename of the given path.
     *
     * @param path a path.
     * @return the basename of the given path.
     */
    @Override
    public String basename(String path) {
        if (path.equals(ROOT_PATH)) {
            return ROOT_PATH;
        }
        return path.substring(path.lastIndexOf(SEPARATOR) + 1);
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
     * @throws NoSuchElementException if the directory does not exist.
     */
    @Override
    public void walk(
            String directory, Predicate<String> excludeDirs, boolean consumeFiles, boolean consumeDirsPreVisit,
            boolean consumeDirsPostVisit, Consumer<String> consumer) {
        Node node = findNode(directory);
        if (node == null) {
            throw new NoSuchElementException("Directory: " + directory + " does not exist.");
        }
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(node);
        Set<DirectoryNode> visited = new HashSet<>();
        while (!stack.isEmpty()) {
            Node currentNode = stack.peek();
            String currentPath = currentNode.path(SEPARATOR, this::join);
            if (currentNode instanceof FileNode) {
                if (consumeFiles) {
                    consumer.accept(currentPath);
                }
                stack.pop();
            } else if (currentNode instanceof DirectoryNode currentDirectory) {
                if (excludeDirs.test(currentPath)) {
                    stack.pop();
                } else {
                    if (!visited.contains(currentDirectory)) {
                        if (consumeDirsPreVisit) {
                            consumer.accept(currentPath);
                        }
                        currentDirectory.children.values().forEach(stack::push);
                        visited.add(currentDirectory);
                    } else {
                        if (consumeDirsPostVisit) {
                            consumer.accept(currentPath);
                        }
                        stack.pop();
                    }
                }
            }
        }
    }

    /**
     * Returns the directory of the given path.
     *
     * @param path a path.
     * @return the directory of the given path, or {@code null} if the given path does not have a parent.
     */
    @Override
    public String getDirectory(String path) {
        if (path.equals(ROOT_PATH)) {
            return null;
        }
        path = removeTrailingSeparator(path);
        String directory = path.substring(0, path.lastIndexOf(SEPARATOR));
        if (directory.isEmpty()) {
            // Path is a child of root.
            return ROOT_PATH;
        }
        return directory;
    }

    /**
     * Returns the given path relative to the given base path.
     *
     * @param base the base path.
     * @param path a path.
     * @return the given path relative to the given base path.
     * @throws IllegalArgumentException if {@code path} does not start with {@code base}.
     */
    @Override
    public String getRelativePath(String base, String path) {
        if (!path.startsWith(base)) {
            throw new IllegalArgumentException(path + " does not start with " + base);
        }
        String relativePath = path.substring(base.length());
        if (relativePath.startsWith(SEPARATOR)) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
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
        base = removeTrailingSeparator(base);
        StringBuilder sb = new StringBuilder(base);
        for (String path : paths) {
            path = removeTrailingSeparator(path);
            sb.append(SEPARATOR).append(path);
        }
        return sb.toString();
    }

    /**
     * Removes {@link InMemoryStorage#SEPARATOR} from the end of the given path if it is present.
     * @param path a path.
     * @return the given path without {@link InMemoryStorage#SEPARATOR} at the end of it.
     */
    private static String removeTrailingSeparator(String path) {
        if (path.endsWith(SEPARATOR)) {
            return path.substring(0, path.length() - SEPARATOR.length());
        } else {
            return path;
        }
    }

    /**
     * Finds the {@link Node} matching the given path.
     * @param path the {@link Node}'s path.
     * @return the {@link Node} matching the given path, or {@code null} if a matching {@link Node} isn't found.
     */
    private Node findNode(String path) {
        Node current = root;
        for (String part : splitPath(path, SEPARATOR)) {
            if (!(current instanceof DirectoryNode dir)) {
                return null;
            }
            current = dir.children.get(part);
        }
        return current;
    }

    /**
     * Creates the given directory and any other intermediate directories if needed.
     * @param path the directory's path.
     * @return the {@link DirectoryNode} of the last directory.
     * @throws IllegalArgumentException if a file exists at an intermediate path.
     */
    private DirectoryNode mkdirs(String path) {
        DirectoryNode currentNode = root;
        String currentPath = SEPARATOR;
        for (String part : splitPath(path, SEPARATOR)) {
            Node child = currentNode.children.get(part);
            if (child == null) {
                DirectoryNode newDirectory = new DirectoryNode(part, currentNode);
                currentNode.children.put(part, newDirectory);
                currentNode = newDirectory;
            } else if (child instanceof DirectoryNode directory) {
                currentNode = directory;
            } else {
                throw new IllegalArgumentException("A file exists at: " + currentPath);
            }
            currentPath = join(currentPath, SEPARATOR);
        }
        return currentNode;
    }
}
