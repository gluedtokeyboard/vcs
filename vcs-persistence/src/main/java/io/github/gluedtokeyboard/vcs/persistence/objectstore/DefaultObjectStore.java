package io.github.gluedtokeyboard.vcs.persistence.objectstore;

import io.github.gluedtokeyboard.vcs.persistence.serialization.Serializer;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * An {@link ObjectStore} implementation using {@link Serializer} and {@link Storage}.
 */
public class DefaultObjectStore implements ObjectStore {
    /**
     * The object used for serialization.
     */
    private final Serializer serializer;

    /**
     * The object used to interact with the storage.
     */
    private final Storage storage;

    /**
     * Stores the given {@link Storage} and {@link Serializer} implementations.
     * @param storage a {@link Storage} implementation.
     * @param serializer a {@link Serializer} implementation.
     */
    public DefaultObjectStore(Storage storage, Serializer serializer) {
        this.storage = storage;
        this.serializer = serializer;
    }

    /**
     * Stores the object at the given location.
     *
     * @param obj      the object to store.
     * @param location the location where the object will be stored.
     */
    @Override
    public void store(Object obj, String location) {
        byte[] bytes = serializer.serialize(obj);
        storage.write(bytes, location);
    }

    /**
     * Loads the object from the given location.
     *
     * @param location the location of the object.
     * @param clazz    the {@link Class} object representing the type {@code T}.
     * @return the object from the given location.
     */
    @Override
    public <T> T load(String location, Class<T> clazz) {
        byte[] bytes = storage.read(location);
        return serializer.deserialize(bytes, clazz);
    }

    /**
     * Deletes the object at the given location.
     *
     * @param location the location of the object.
     */
    @Override
    public void delete(String location) {
        storage.rm(location);
    }
}
