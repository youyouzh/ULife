package com.uusama.common.util;

/**
 * @author uusama
 */
public class ObjectUtil {

    @SafeVarargs
    public static <T> T defaultIfNull(T... array) {
        for (T item : array) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }
}
