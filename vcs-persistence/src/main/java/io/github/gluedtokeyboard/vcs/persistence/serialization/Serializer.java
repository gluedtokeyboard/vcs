package io.github.gluedtokeyboard.vcs.persistence.serialization;

/**
 * Defines serialization methods.
 */
public interface Serializer {
    /**
     * Serializes the given object.
     * @param obj the object to serialize.
     * @return the serialized bytes.
     */
    byte[] serialize(Object obj);

    /**
     * Deserializes the given bytes.
     * @param bytes the bytes to deserialize.
     * @param clazz the {@link Class} object representing the type {@code T}.
     * @return the deserialized object.
     * @param <T> the type of the object.
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
