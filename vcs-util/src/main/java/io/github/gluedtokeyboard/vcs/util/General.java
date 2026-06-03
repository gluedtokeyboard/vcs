package io.github.gluedtokeyboard.vcs.util;

import java.util.*;
import java.util.function.Function;

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

    /**
     * Computes the set intersection between two sets.
     * @param a the first set.
     * @param b the second set.
     * @return a new set containing the intersection.
     * @param <T> the type of the set elements.
     */
    public static <T> Set<T> setIntersection(Set<T> a, Set<T> b) {
        Set<T> smallerSet, biggerSet;
        if (a.size() < b.size()) {
            smallerSet = a;
            biggerSet = b;
        } else {
            smallerSet = b;
            biggerSet = a;
        }
        Set<T> intersection = new HashSet<>();
        smallerSet.stream().filter(biggerSet::contains).forEach(intersection::add);
        return intersection;
    }

    /**
     * Computes the set union between a base set and other sets.
     * @param base the base set.
     * @param others the sets whose elements should be added to the base set.
     * @return a new set containing the union.
     * @param <T> the type of the set elements.
     */
    @SafeVarargs
    public static <T> Set<T> setUnion(Set<T> base, Set<T>... others) {
        Set<T> union = new HashSet<>(base);
        for (Set<T> other : others) {
            union.addAll(other);
        }
        return union;
    }

    /**
     * Splits the given path using the given separator.
     *
     * @param path the path to split
     * @param separator the separator to use to split the path
     * @return the parts of the given path
     */
    public static List<String> splitPath(String path, String separator) {
        return Arrays.stream(path.split(separator))
                .filter(part -> !part.isEmpty())
                .toList();
    }

    /**
     * Maps each element of a {@linkplain Collection} using the given {@linkplain Function}.
     *
     * @param collection the source {@linkplain Collection}
     * @param mapper the mapping {@linkplain Function}
     * @return the mapped elements
     * @param <T> the type of the {@linkplain Collection} elements
     * @param <R> the result type
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        return collection.stream().map(mapper).toList();
    }
}
