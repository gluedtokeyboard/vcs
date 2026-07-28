package io.github.gluedtokeyboard.vcs.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class StorageContractTest {
    private Storage storage;

    abstract protected Storage createStorage();

    @BeforeEach
    void setUp() {
        storage = createStorage();
    }

    @Test
    @DisplayName("Write file and then read it")
    void shouldWriteAndReadFile() {
        storage.write("hello", "/a.txt");
        assertEquals("hello", new String(storage.read("/a.txt")));
    }

    @Test
    @DisplayName("Overwrite a file and then read it")
    void shouldOverwriteExistingFile() {
        String initialContents = "hello";
        String file = "/a.txt";
        storage.write(initialContents, file);
        byte[] initialBytes = storage.read(file);
        assertEquals(initialContents, new String(initialBytes));
        String updatedContents = "hello world";
        storage.write(updatedContents, file);
        byte[] updatedBytes = storage.read(file);
        assertEquals(updatedContents, new String(updatedBytes));
    }

    @Test
    @DisplayName("Check file existence")
    void shouldCheckExists() {
        storage.write("x", "/file.txt");
        assertTrue(storage.exists("/file.txt"));
        assertFalse(storage.exists("/missing.txt"));
    }

    @Test
    @DisplayName("Create directories")
    void shouldCreateDirectories() {
        storage.mkdir("/a/b/c");
        storage.mkdir("/a/b/c/d");
        assertTrue(storage.exists("/a/b/c"));
        assertTrue(storage.exists("/a/b/c/d"));
    }

    @Test
    @DisplayName("Remove file")
    void shouldRemoveFile() {
        storage.write("x", "/file.txt");
        storage.rm("/file.txt");
        assertFalse(storage.exists("/file.txt"));
    }

    @Test
    @DisplayName("Should remove a non-empty directory")
    void shouldRemoveNonEmptyDirectory() {
        String directory = "/a";
        String file = directory + "/file.txt";
        Storage storage = createStorage();
        storage.mkdir(directory);
        storage.write("x", file);
        storage.rm(directory);
        assertFalse(storage.exists(directory));
    }

    @Test
    @DisplayName("List files")
    void shouldListFiles() {
        storage.write("", "/a.txt");
        storage.mkdir("/a");
        Set<Entry> actualEntries = new HashSet<>(storage.ls("/"));
        Set<Entry> expectedEntries = Set.of(
                new Entry("/a.txt", EntryType.FILE),
                new Entry("/a", EntryType.DIRECTORY)
        );
        assertEquals(expectedEntries, actualEntries);
    }

    @Test
    @DisplayName("Move file")
    void shouldMoveFile() {
        storage.write("x", "/a.txt");
        storage.mv("/a.txt", "/b.txt");
        assertFalse(storage.exists("/a.txt"));
        assertTrue(storage.exists("/b.txt"));
    }

    @Test
    @DisplayName("Move directory")
    void shouldMoveDirectory() {
        setupStorage(storage);
        String source = "/a/b/e";
        String destination = "/a/c/f";
        HashSet<String> sourceContents = getDirectoryContentsNested(source);
        storage.mv(source, destination);
        HashSet<String> destinationContents = getDirectoryContentsNested(destination);
        assertEquals(sourceContents, destinationContents);
    }

    /**
     * Returns the contents of the given directory along with the contents of all of its subdirectories. The path of
     * the source directory is stripped out from the contents to make it easier to check if the contents of two
     * directories are equal.
     *
     * @param directory the source directory
     * @return the contents of the given directory
     */
    private HashSet<String> getDirectoryContentsNested(String directory) {
        HashSet<String> contents = new HashSet<>();
        storage.walk(directory, (_) -> false, true, true, false, (path) -> {
            path = path.replaceFirst("^" + directory, "");
            contents.add(path);
        });
        return contents;
    }

    @Test
    @DisplayName("Basename")
    void shouldReturnBasename() {
        assertEquals("file.txt", storage.basename("/a/b/file.txt"));
    }

    @Test
    @DisplayName("Walk directory: Pre visit directories")
    void shouldWalkDirectoryPreVisit() {
        setupStorage(storage);
        String staringDirectory = "/a";
        List<String> order = new ArrayList<>();
        storage.walk(
                staringDirectory,
                _ -> false,
                true,
                true,
                false,
                path -> {
                    String basename = storage.basename(path);
                    order.add(basename);
                }
        );
        assertTrue(order.indexOf("a") < order.indexOf("b"));
        assertTrue(order.indexOf("a") < order.indexOf("c"));
        assertTrue(order.indexOf("a") < order.indexOf("d"));
        assertTrue(order.indexOf("b") < order.indexOf("e"));
        assertTrue(order.indexOf("b") < order.indexOf("f"));
        assertTrue(order.indexOf("b") < order.indexOf("g"));
        assertTrue(order.indexOf("c") < order.indexOf("h"));
        assertTrue(order.indexOf("e") < order.indexOf("i"));
        assertTrue(order.indexOf("e") < order.indexOf("j"));
    }

    @Test
    @DisplayName("Walk directory: Post visit directories")
    void shouldWalkDirectoryPostVisit() {
        setupStorage(storage);
        String startingDirectory = "/a";
        List<String> order = new ArrayList<>();
        storage.walk(
                startingDirectory,
                _ -> false,
                true,
                false,
                true,
                path -> {
                    String basename = storage.basename(path);
                    order.add(basename);
                }
        );
        assertTrue(order.indexOf("a") > order.indexOf("b"));
        assertTrue(order.indexOf("a") > order.indexOf("c"));
        assertTrue(order.indexOf("a") > order.indexOf("d"));
        assertTrue(order.indexOf("b") > order.indexOf("e"));
        assertTrue(order.indexOf("b") > order.indexOf("f"));
        assertTrue(order.indexOf("b") > order.indexOf("g"));
        assertTrue(order.indexOf("c") > order.indexOf("h"));
        assertTrue(order.indexOf("e") > order.indexOf("i"));
        assertTrue(order.indexOf("e") > order.indexOf("j"));
    }

    @Test
    @DisplayName("Walk directory: Pre and post visit directories")
    void shouldWalkDirectoryPreAndPostVisit() {
        setupStorage(storage);
        String startingDirectory = "/a";
        List<String> order = new ArrayList<>();
        storage.walk(
                startingDirectory,
                _ -> false,
                true,
                true,
                true,
                path -> {
                    String basename = storage.basename(path);
                    order.add(basename);
                }
        );
        assertEquals(13, order.lastIndexOf("a") - order.indexOf("a"));
        assertEquals(7, order.lastIndexOf("b") - order.indexOf("b"));
        assertEquals(2, order.lastIndexOf("c") - order.indexOf("c"));
        assertEquals(3, order.lastIndexOf("e") - order.indexOf("e"));
    }

    @Test
    @DisplayName("Walk directory: Don't consume directories")
    void shouldWalkDirectoryWithoutConsumingDirectories() {
        setupStorage(storage);
        String startingDirectory = "/a";
        Set<String> order = new HashSet<>();
        storage.walk(
                startingDirectory,
                _ -> false,
                true,
                false,
                false,
                path -> {
                    String basename = storage.basename(path);
                    order.add(basename);
                }
        );
        assertEquals(Set.of("d", "f", "g", "h", "i", "j"), order);
    }

    @Test
    @DisplayName("Walk directory: Don't consume anything")
    void shouldWalkDirectoryWithoutConsumingAnything() {
        setupStorage(storage);
        String startingDirectory = "/a";
        Set<String> order = new HashSet<>();
        storage.walk(
                startingDirectory,
                _ -> false,
                false,
                false,
                false,
                path -> {
                    String basename = storage.basename(path);
                    order.add(basename);
                }
        );
        assertTrue(order.isEmpty());
    }

    @Test
    @DisplayName("Walk directory: Skip one directory")
    void shouldWalkDirectoryAndSkipOneDirectory() {
        setupStorage(storage);
        String startingDirectory = "/a";
        Set<String> order = new HashSet<>();
        storage.walk(
                startingDirectory,
                path -> storage.basename(path).equals("b"),
                true,
                true,
                false,
                path -> {
                    String basename = storage.basename(path);
                    order.add(basename);
                }
        );
        assertEquals(Set.of("a", "c", "h", "d"), order);
    }

    /**
     * Sets up the given {@link Storage} with sample files and directories. The storage will contain:
     * <pre>
     *                           root
     *                            |
     *                            a
     *                ____________|____________
     *               /            |            \
     *              b             c             d
     *         _____|_____        |
     *        /     |     \       |
     *       e      f      g      h
     *     __|__
     *    /     \
     *   i       j
     * </pre>
     * @param storage the {@link Storage} to set up.
     */
    private void setupStorage(Storage storage) {
        storage.mkdir("/a/b");
        storage.mkdir("/a/c");
        storage.write("", "/a/d");
        storage.mkdir("/a/b/e");
        storage.write("", "/a/b/f");
        storage.write("", "/a/b/g");
        storage.write("", "/a/c/h");
        storage.write("", "/a/b/e/i");
        storage.write("", "/a/b/e/j");
    }

    @Test
    @DisplayName("Get directory")
    void shouldGetDirectory() {
        String path1 = "/a";
        String directory1 = "/";
        String path2 = "/a/b";
        String directory2 = "/a";
        assertEquals(directory1, storage.getDirectory(path1));
        assertEquals(directory2, storage.getDirectory(path2));
    }

    @Test
    @DisplayName("Get the root's directory")
    void shouldGetRootsDirectory() {
        assertNull(storage.getDirectory("/"));
    }

    @Test
    @DisplayName("Relative path")
    void shouldGetRelativePath() {
        String base = "/a/b/c";
        String path = "/a/b/c/d";
        assertEquals("d", storage.getRelativePath(base, path));
    }

    @Test
    @DisplayName("Relative path against root")
    void shouldGetRelativePathAgainstRoot() {
        String base = "/";
        String path = "/a";
        assertEquals("a", storage.getRelativePath(base, path));
    }

    @Test
    @DisplayName("Join paths")
    void shouldJoinPaths() {
        assertEquals("/a/b/c", storage.join("/a", "b", "c"));
    }
}
