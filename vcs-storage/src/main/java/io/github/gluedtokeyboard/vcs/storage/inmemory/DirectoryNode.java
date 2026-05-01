package io.github.gluedtokeyboard.vcs.storage.inmemory;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a directory node in a filesystem tree.
 */
public class DirectoryNode extends Node {
    /**
     * Contains the children of the directory.
     */
    final Map<String, Node> children = new HashMap<>();

    /**
     * Stores the directory's name, path, and parent.
     * @param name the name of the directory.
     * @param parent the directory's parent.
     */
    public DirectoryNode(String name, DirectoryNode parent) {
        super(name, parent);
    }
}
