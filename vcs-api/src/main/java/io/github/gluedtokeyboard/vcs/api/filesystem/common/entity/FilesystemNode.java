package io.github.gluedtokeyboard.vcs.api.filesystem.common.entity;

import io.github.gluedtokeyboard.vcs.api.common.idgenerator.UuidV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A filesystem node.
 */
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "path")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FilesystemNode {
    /**
     * The ID used in requests/reponses.
     */
    @Id
    @UuidV7
    private UUID id;

    /**
     * The path of the node.
     */
    @Setter
    private String path;

    /**
     * Creates the object without an ID.
     *
     * @param path the path of the node
     */
    public FilesystemNode(String path) {
        this.path = path;
    }
}
