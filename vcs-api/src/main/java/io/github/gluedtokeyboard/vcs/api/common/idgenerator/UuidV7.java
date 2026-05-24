package io.github.gluedtokeyboard.vcs.api.common.idgenerator;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity identifier field to use UUIDv7 generation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(UuidV7Generator.class)
public @interface UuidV7 {
}
