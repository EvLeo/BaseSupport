package com.leo.support.network.utils;

import com.leo.support.bean.KeyValuePair;

import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by leo on 2017/6/12.
 */

public class HttpUtil {

    public static DefaultHttpClient createHttpClient(int timeout) {
        HttpParams httpParams = createHttpParams(timeout);
        return new DefaultHttpClient(httpParams);
    }

    private static HttpParams createHttpParams (int timeout) {
        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, timeout * 1000);
        HttpConnectionParams.setSoTimeout(params, timeout * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 1024 * 10);
        HttpConnectionParams.setTcpNoDelay(params, false);

        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(32));
        ConnManagerParams.setMaxTotalConnections(params, 256);

        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
        return params;

    }

    public static String encodeUrl(String url, List<KeyValuePair> params) {
        if (null == params || params.size() == 0) {
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
            sb.append(encodeUrl(pair.getKey())).append("=")
                    .append(encodeUrl(pair.getValue()));
        }
        return sb.toString();
    }

    public static String encodeUrl(String value) {
        String encoded;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded = value;
        }
        StringBuilder sb = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                sb.append("%2A");
            } else if (focus == '+') {
                sb.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(focus);
            }
        }
        return sb.toString();
    }
}
