package io.github.gluedtokeyboard.vcs.storage.inmemory;

import java.util.*;

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

    /**
     * Creates a deep copy of the current {@linkplain DirectoryNode}.
     *
     * @return a deep copy of the current {@linkplain DirectoryNode}
     */
    DirectoryNode copy() {
        // `sentinel` will have only one child: the copy of `this`.
        DirectoryNode sentinel = new DirectoryNode(null, null);
        /*
        - A stack containing partial copies of directories.
        - The head of the stack will always be the copy of the directory whose children are currently being cloned.
         */
        ArrayDeque<DirectoryNode> partiallyCopiedDirectories = new ArrayDeque<>();
        partiallyCopiedDirectories.push(sentinel);
        ArrayDeque<Node> toBeCopied = new ArrayDeque<>();
        toBeCopied.push(this);
        Set<DirectoryNode> seenDirectories = new HashSet<>();
        while (!toBeCopied.isEmpty()) {
            Node currentNode = toBeCopied.peek();
            DirectoryNode currentParentCopy = partiallyCopiedDirectories.peek();
            assert currentParentCopy != null;
            if (currentNode instanceof DirectoryNode currentDirectory) {
                DirectoryNode currentDirectoryCopy;
                if (!seenDirectories.contains(currentDirectory)) {
                    currentDirectoryCopy = new DirectoryNode(currentDirectory.name, currentParentCopy);
                    partiallyCopiedDirectories.push(currentDirectoryCopy);
                    currentDirectory.children.values().forEach(toBeCopied::push);
                    seenDirectories.add(currentDirectory);
                } else {
                    currentDirectoryCopy = partiallyCopiedDirectories.pop();
                    currentParentCopy = partiallyCopiedDirectories.peek();
                    assert currentParentCopy != null;
                    currentParentCopy.children.put(currentDirectoryCopy.name, currentDirectoryCopy);
                    toBeCopied.pop();
                }
            } else {
                Node currentNodeCopy = currentNode.copy();
                currentNodeCopy.parent = currentParentCopy;
                currentParentCopy.children.put(currentNode.name, currentNodeCopy);
                toBeCopied.pop();
            }
        }
        return (DirectoryNode) sentinel.children.get(name);
    }
}
