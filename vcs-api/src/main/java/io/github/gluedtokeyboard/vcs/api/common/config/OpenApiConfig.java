package io.github.gluedtokeyboard.vcs.api.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "VCS",
                version = "1.0.0",
                description = "The documentation for the REST API."
        ),
        tags = {
                @Tag(
                        name = "Repository",
                        description = "Create repositories, search for repositories upwards from a given directory, " +
                                "and retrieve repository status."
                ),
                @Tag(
                        name = "Staging Area",
                        description = "Stage files for addition or removal."
                ),
                @Tag(
                        name = "Commit",
                        description = "Create normal or merge commits, and retrieve commit logs."
                ),
                @Tag(
                        name = "Branch",
                        description = "Create or delete branches."
                ),
                @Tag(
                        name = "HEAD",
                        description = "Move HEAD to a specific commit."
                ),
                @Tag(
                        name = "Working Tree",
                        description = "Reset the working tree to a specific commit, or checkout a file from a " +
                                "specific commit."
                ),
                @Tag(
                        name = "Directory",
                        description = "Create, retrieve, rename, move, copy, and delete directories, and list their " +
                                "contents."
                ),
                @Tag(
                        name = "File",
                        description = "Create, retrieve, update, rename, move, copy, and delete files."
                )
        }
)
public class OpenApiConfig {
}
