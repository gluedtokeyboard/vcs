package io.github.gluedtokeyboard.vcs.persistence.serialization;

import java.io.*;

/**
 * Serializes {@link Serializable} objects.
 */
public class JavaBinarySerializer implements Serializer {
    /**
     * Serializes the given object.
     *
     * @param obj the object to serialize. it must implement {@link Serializable}.
     * @return the serialized bytes.
     * @throws IllegalArgumentException if the given object does not implement {@link Serializable}.
     */
    @Override
    public byte[] serialize(Object obj) {
        if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("The given object does not implement Serializable");
        }
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes the given bytes.
     *
     * @param bytes the bytes to deserialize.
     * @param clazz the {@link Class} object representing the type {@code T}.
     * @return the deserialized object.
     * @throws IllegalArgumentException if the given class does not implement {@link Serializable}.
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (!Serializable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The given class does not implement Serializable");
        }
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis)
        ) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
