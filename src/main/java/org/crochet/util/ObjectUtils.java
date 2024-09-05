package org.crochet.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class ObjectUtils {
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = "{}";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final String NON_EMPTY_ARRAY = "{...}";
    private static final String COLLECTION = "[...]";
    private static final String MAP = "{...}";

    /**
     * Is empty
     *
     * @param array Object[]
     * @return Boolean
     */
    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Is empty
     *
     * @param obj Object
     * @return boolean
     */
    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            Optional<?> optional = (Optional) obj;
            return optional.isEmpty();
        } else if (obj instanceof CharSequence charSequence) {
            return charSequence.isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            Collection<?> collection = (Collection) obj;
            return collection.isEmpty();
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map) obj;
            return map.isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Is not empty
     *
     * @param array Object[]
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !isEmpty(array);
    }

    /**
     * Is not empty
     *
     * @param obj Object[]
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Object obj) {
        return !isEmpty(obj);
    }
}
