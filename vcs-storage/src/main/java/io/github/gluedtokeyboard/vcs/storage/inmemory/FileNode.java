package io.github.gluedtokeyboard.vcs.storage.inmemory;

import java.util.Arrays;

/**
 * Represents a file node in a filesystem tree.
 */
public class FileNode extends Node {
    /**
     * The contents of the file.
     */
    byte[] contents;

    /**
     * Stores the file's name, path, parent, and contents.
     * @param name the name of the file.
     * @param parent the file's parent.
     * @param contents the contents of the file.
     */
    FileNode(String name, DirectoryNode parent, byte[] contents) {
        super(name, parent);
        this.contents = contents;
    }

    /**
     * Creates a copy of the current {@linkplain FileNode}.
     *
     * @return a copy of the current {@linkplain FileNode}
     */
    FileNode copy() {
        byte[] newContents = Arrays.copyOf(contents, contents.length);
        return new FileNode(name, parent, newContents);
    }
}
