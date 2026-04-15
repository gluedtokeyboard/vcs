package vcs.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * General utils.
 */
public final class General {
    /**
     * Computes the set difference between a base set and other sets.
     * @param base the base set.
     * @param others the sets whose elements should be removed from the base set.
     * @return a new set containing the difference.
     * @param <T> the type of the set elements.
     */
    @SafeVarargs
    public static <T> Set<T> setDifference(Set<T> base, Set<T>... others) {
        Set<T> difference = new HashSet<>(base);
        for (Set<T> other : others) {
            difference.removeAll(other);
        }
        return difference;
    }
}
