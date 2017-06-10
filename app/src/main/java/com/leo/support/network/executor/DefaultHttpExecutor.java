package com.leo.support.network.executor;

import com.leo.support.network.HttpExecutor;
import com.leo.support.network.HttpListener;
import com.leo.support.network.HttpResult;

/**
 * Created by leo on 2017/6/10.
 */

public class DefaultHttpExecutor implements HttpExecutor {
    @Override
    public HttpResult doGet(String url, HttpRequestParams params, HttpListener httpListener) {
        return null;
    }

    @Override
    public HttpResult doPost(String url, HttpRequestParams params, HttpListener httpListener) {
        return null;
    }
}
