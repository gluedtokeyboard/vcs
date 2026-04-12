package vcs.core;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

import static vcs.utils.FileSystem.*;

/**
 * The {@link VCS} class stores all metadata about the repository.
 */
public class VCS implements Serializable {
    /**
     * The folder used to store the repository data and metadata.
     */
    private static final String REPO_FOLDER = ".vcs";

    /**
     * The staging area folder. This will contain {@link Blob} objects of the staged files.
     */
    private static final String STAGING_AREA_FOLDER = "index";

    /**
     * The folder containing {@link Commit} objects.
     */
    private static final String COMMITS_FOLDER = "commits";

    /**
     * The folder containing {@link Blob} objects of the committed files.
     */
    private static final String BLOBS_FOLDER = "blobs";

    /**
     * The name to use for serialization/deserialization.
     */
    private static final String OBJECT_NAME = "vcs";

    /**
     * The name of the current branch.
     */
    private String currentBranch = "main";

    /**
     * Maps the branch name to the SHA-1 hash of the branch head.
     */
    private final Map<String, String> branchMap = new HashMap<>();

    /**
     * Maps the staged file names to their SHA-1 hashes. See
     * {@link Commit#Commit(String, Date, String, String, TreeMap)} for why {@link TreeMap} is used.
     */
    private final TreeMap<String, String> stagedFiles = new TreeMap<>();

    /**
     * Initializes the repository object with a default branch named "main".
     */
    public VCS() {
        branchMap.put("main", null);
    }

    /*
     * Resolves the given path relative to the repository folder.
     * @param path the relative path.
     * @return the absolute path obtained by resolving the given path against the repository folder.
     */
    private static Path resolvePath(String path) {
        return join(getCWD(), REPO_FOLDER, path);
    }

    /**
     * Serializes the current object and writes it to {@link VCS#REPO_FOLDER}/{@link VCS#OBJECT_NAME}.
     */
    public void serialize() {
        Path path = resolvePath(OBJECT_NAME);
        vcs.utils.Serialization.serialize(this, path);
    }

    /**
     * Deserializes the {@link VCS} object from {@link VCS#REPO_FOLDER}/{@link VCS#OBJECT_NAME}.
     * @return The deserialized {@link VCS} object.
     */
    public static VCS deserialize() {
        Path path = resolvePath(OBJECT_NAME);
        return vcs.utils.Serialization.deserialize(path, VCS.class);
    }

    /**
     * Creates a repository in the current working directory.
     */
    public void createRepo() {
        createRepoFolders();
        serialize();
    }

    /**
     * Creates the folders required to store the data and the metadata of the repository.
     */
    private void createRepoFolders() {
        List<String> folders = List.of(
                STAGING_AREA_FOLDER,
                COMMITS_FOLDER,
                BLOBS_FOLDER
        );
        for (String folder : folders) {
            mkdir(resolvePath(folder));
        }
    }
}
