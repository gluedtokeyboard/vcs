package vcs.utils.stateless;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utils for serialization/deserialization.
 */
public final class Serialization {
    /**
     * Serializes the given object and writes it at the given path.
     * @param object the object to serialize.
     * @param path the path where the object will be stored.
     */
    public static void serialize(Serializable object, Path path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes the object at {@code path} into an instance of the given class.
     * @param path the path of the object.
     * @param className the class of the object.
     * @return the deserialized object.
     * @param <T> typeVar for the class of the object.
     */
    public static <T extends Serializable> T deserialize(Path path, Class<T> className) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return className.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
