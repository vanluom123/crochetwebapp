package org.crochet.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for common Object operations.
 * Extends functionality from Apache Commons Lang3 ObjectUtils.
 */
@SuppressWarnings("ALL")
public final class ObjectUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ObjectUtils() {
    }

    /**
     * Checks if an object is null.
     *
     * @param obj the object to check
     * @return true if the object is null, false otherwise
     */
    public static boolean isNull(final Object obj) {
        return obj == null;
    }

    /**
     * Checks if an object is not null.
     *
     * @param obj the object to check
     * @return true if the object is not null, false otherwise
     */
    public static boolean isNotNull(final Object obj) {
        return obj != null;
    }

    /**
     * Checks if an object, collection, map, or array is empty or null.
     *
     * @param obj the object to check
     * @return true if the object is empty or null, false otherwise
     */
    public static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }

        if (obj instanceof Optional) {
            return ((Optional<?>) obj).isEmpty();
        }

        return false;
    }

    /**
     * Checks if an object, collection, map, or array is not empty and not null.
     *
     * @param obj the object to check
     * @return true if the object is not empty and not null, false otherwise
     */
    public static boolean isNotEmpty(final Object obj) {
        return !isEmpty(obj);
    }

    /**
     * Returns the first non-null value from an array of objects.
     *
     * @param <T>    the type of the objects
     * @param values the array of objects to check
     * @return the first non-null value, or null if all values are null
     */
    @SafeVarargs
    public static <T> T firstNonNull(final T... values) {
        if (values != null) {
            for (final T value : values) {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Returns the default value if the object is null.
     *
     * @param <T>          the type of the objects
     * @param obj          the object to check
     * @param defaultValue the default value to return if obj is null
     * @return obj if not null, defaultValue otherwise
     */
    public static <T> T defaultIfNull(final T obj, final T defaultValue) {
        return obj != null ? obj : defaultValue;
    }

    /**
     * Deep equality check between two objects.
     *
     * @param o1 the first object
     * @param o2 the second object
     * @return true if the objects are deeply equal, false otherwise
     */
    public static boolean equals(final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public static boolean notEqual(final Object o1, final Object o2) {
        return !equals(o1, o2);
    }

    /**
     * Safely gets a value from a collection by index, returning defaultValue if out of bounds.
     *
     * @param <T>          the type of elements in the collection
     * @param collection   the collection to get the element from
     * @param index        the index of the element to get
     * @param defaultValue the default value to return if the index is out of bounds
     * @return the element at the specified index, or defaultValue if index is out of bounds
     */
    public static <T> T getOrDefault(final List<T> collection, final int index, final T defaultValue) {
        if (isEmpty(collection) || index < 0 || index >= collection.size()) {
            return defaultValue;
        }
        return collection.get(index);
    }

    /**
     * Converts a collection to a map using the provided key and value extractors.
     *
     * @param <T>            the type of elements in the collection
     * @param <K>            the type of keys in the map
     * @param <V>            the type of values in the map
     * @param collection     the collection to convert
     * @param keyExtractor   the function to extract keys from elements
     * @param valueExtractor the function to extract values from elements
     * @return a map containing the extracted keys and values
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection,
                                            Function<? super T, ? extends K> keyExtractor,
                                            Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }
        return collection.stream()
                .collect(Collectors.toMap(keyExtractor, valueExtractor, (v1, v2) -> v1));
    }

    /**
     * Safely gets a value from a map by key, returning defaultValue if key not found.
     *
     * @param <K>          the type of keys in the map
     * @param <V>          the type of values in the map
     * @param map          the map to get the value from
     * @param key          the key to look up
     * @param defaultValue the default value to return if the key is not found
     * @return the value associated with the key, or defaultValue if the key is not found
     */
    public static <K, V> V getOrDefault(final Map<K, V> map, final K key, final V defaultValue) {
        if (isEmpty(map) || key == null) {
            return defaultValue;
        }
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * Safely checks if a collection contains an element.
     *
     * @param <T>        the type of elements in the collection
     * @param collection the collection to check
     * @param element    the element to look for
     * @return true if the collection contains the element, false otherwise or if collection is null
     */
    public static <T> boolean contains(final Collection<T> collection, final T element) {
        return collection != null && collection.contains(element);
    }

    /**
     * Creates a defensive copy of a list.
     *
     * @param <T>  the type of elements in the list
     * @param list the list to copy
     * @return an unmodifiable copy of the list, or an empty list if the input is null
     */
    public static <T> List<T> defensiveCopy(final List<T> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(list));
    }

    /**
     * Safely executes a function on an object if the object is not null.
     *
     * @param <T>          the type of the object
     * @param <R>          the return type of the function
     * @param obj          the object to operate on
     * @param function     the function to execute
     * @param defaultValue the default value to return if obj is null
     * @return the result of the function, or defaultValue if obj is null
     */
    public static <T, R> R applyIfNotNull(T obj, Function<T, R> function, R defaultValue) {
        return obj != null ? function.apply(obj) : defaultValue;
    }

    /**
     * Checks if a string has text (not null, not empty, not only whitespace).
     *
     * @param str the string to check
     * @return true if the string has text, false otherwise
     */
    public static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Safely compares two comparable objects, handling null values.
     *
     * @param <T> the type of objects being compared
     * @param o1  the first object
     * @param o2  the second object
     * @return negative if o1 < o2, 0 if equal, positive if o1 > o2, with nulls sorting first
     */
    public static <T extends Comparable<T>> int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }
}