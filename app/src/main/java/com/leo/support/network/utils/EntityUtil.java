package com.leo.support.network.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;

/**
 * Created by leo on 2017/6/14.
 */

public class EntityUtil {

    public static boolean isGZIPed(HttpEntity entity)
            throws IllegalStateException, IOException {
        if (null == entity) {
            return false;
        }
        Header header = entity.getContentEncoding();
        if (null == header) {
            return false;
        }
        String contentEncoding = header.getValue();
        return contentEncoding != null && contentEncoding.contains("gzip");
    }
}
