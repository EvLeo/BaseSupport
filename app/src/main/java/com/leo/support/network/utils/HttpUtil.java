package com.leo.support.network.utils;

import com.leo.support.bean.KeyValuePair;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

/**
 * Created by leo on 2017/6/12.
 */

public class HttpUtil {

    public static final DefaultHttpClient createHttpClient(int timeout) {
        return null;
    }

    public static String encodeUrl(String url, List<KeyValuePair> params) {
        if (null != params || params.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        if (url.contains("?")) {
            sb.append(url).append("&");
        } else {
            sb.append(url).append("?");
        }
        for (int i = 0; i < params.size(); i++) {
            KeyValuePair pair = params.get(i);
            if (i > 0) {
                sb.append("&");
            }
            sb.append("");
        }
        return sb.toString();
    }
}
