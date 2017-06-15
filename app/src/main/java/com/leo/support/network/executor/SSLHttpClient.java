package com.leo.support.network.executor;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

/**
 * Created by leo on 2017/6/15.
 */

public class SSLHttpClient extends DefaultHttpClient {

    public SSLHttpClient(HttpParams params) {
        super(params);
    }

    public synchronized static SSLHttpClient getInstance(HttpParams params) {
        return new SSLHttpClient(params);
    }
}
