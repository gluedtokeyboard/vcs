package io.github.gluedtokeyboard.vcs.api.filesystem.common.repository;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link FilesystemNode}.
 */
@Repository
public interface FilesystemNodeRepository extends JpaRepository<FilesystemNode, UUID> {
    /**
     * Retrieves a {@linkplain FilesystemNode} by its path.
     *
     * @param path the path of the {@linkplain FilesystemNode}
     * @return the {@linkplain FilesystemNode} with the given path if present
     */
    Optional<FilesystemNode> findByPath(String path);

    /**
     * Checks if a {@linkplain FilesystemNode} with the given path exists.
     *
     * @param path the path to find
     * @return a boolean indicating whether a {@linkplain FilesystemNode} with the given path exists
     */
    boolean existsByPath(String path);
}
