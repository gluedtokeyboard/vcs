package io.github.gluedtokeyboard.vcs.persistence.objectstore;

/**
 * Defines methods to load, store, and delete objects.
 */
public interface ObjectStore {
    /**
     * Stores the object at the given location.
     * @param obj the object to store.
     * @param location the location where the object will be stored.
     */
    void store(Object obj, String location);

    /**
     * Loads the object from the given location.
     * @param location the location of the object.
     * @param clazz the {@link Class} object representing the type {@code T}.
     * @return the object from the given location.
     * @param <T> the type of the object.
     */
    <T> T load(String location, Class<T> clazz);

    /**
     * Deletes the object at the given location.
     * @param location the location of the object.
     */
    void delete(String location);
}
