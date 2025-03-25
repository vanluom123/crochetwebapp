package org.crochet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for common Object operations.
 * Extends functionality from Apache Commons Lang3 ObjectUtils.
 */
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

    /**
     * Converts an object to JSON string using the provided ObjectMapper.
     *
     * @param object the object to convert
     * @param mapper the ObjectMapper to use
     * @return the JSON string or null if conversion failed
     */
    public static String toJson(Object object, ObjectMapper mapper) {
        if (object == null || mapper == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * Parses a JSON string to an object using the provided ObjectMapper.
     *
     * @param <T> the type of the target object
     * @param json the JSON string to parse
     * @param clazz the class of the target type
     * @param mapper the ObjectMapper to use
     * @return the parsed object or null if parsing failed
     */
    public static <T> T fromJson(String json, Class<T> clazz, ObjectMapper mapper) {
        if (!hasText(json) || clazz == null || mapper == null) {
            return null;
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    /**
     * Copies only non-null properties from source to target object.
     *
     * @param source the source object
     * @param target the target object
     */
    public static void copyNonNullProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * Gets an array of property names that have null values in the object.
     *
     * @param source the object to check
     * @return array of property names with null values
     */
    private static String[] getNullPropertyNames(Object source) {
        final org.springframework.beans.BeanWrapper src =
            new org.springframework.beans.BeanWrapperImpl(source);

        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullNames = new HashSet<>();

        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullNames.add(pd.getName());
            }
        }

        return nullNames.toArray(new String[0]);
    }

}
