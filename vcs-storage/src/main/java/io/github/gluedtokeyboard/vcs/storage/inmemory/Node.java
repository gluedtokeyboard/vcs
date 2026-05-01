package io.github.gluedtokeyboard.vcs.storage.inmemory;

import java.util.ArrayDeque;
import java.util.function.BiFunction;

/**
 * Represents a node in a filesystem tree.
 */
public abstract class Node {
    /**
     * The name of the node.
     */
    String name;

    /**
     * The node's parent.
     */
    DirectoryNode parent;

    /**
     * Stores the node's name, path, and parent.
     * @param name the name of the node.
     * @param parent the node's parent.
     */
    Node(String name, DirectoryNode parent) {
        this.name = name;
        this.parent = parent;
    }

    /**
     * Returns the path of the node.
     *
     * @param separator the path separator
     * @param pathJoiner a function that joins path fragments to a base path
     * @return the path of the node
     */
    String path(String separator, BiFunction<String, String[], String> pathJoiner) {
        Node current = this;
        ArrayDeque<String> pathFragments = new ArrayDeque<>();
        while (current != null) {
            if (!current.name.isEmpty()) {
                pathFragments.addFirst(current.name);
            }
            current = current.parent;
        }
        return pathJoiner.apply(separator, pathFragments.toArray(String[]::new));
    }

    /**
     * A string representation of the {@linkplain Node}, which consists of its runtime class name followed by the
     * {@linkplain Node}'s name.
     *
     * @return a string representation of the {@linkplain Node}
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + name;
    }
}
