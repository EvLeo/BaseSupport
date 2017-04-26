package com.leo.support.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by LiuYu on 2017/4/26.
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
