package com.leo.support.network;

import com.leo.support.bean.KeyValuePair;
import com.leo.support.config.BaseConfig;
import com.leo.support.network.listener.DataHttpListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * done
 * Created by leo on 2017/6/10.
 */

public class HttpProvider {
    private boolean isProxy = true;

    public void setFlowProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    public HttpProvider() {
    }

    private HttpExecutor getHttpExecutor() {
        return BaseConfig.getConfig().getHttpExecutor();
    }

    public HttpResult doGet(String url, int timeout, HttpListener listener) {
        return doGet(url, timeout, -1, listener);
    }

    public HttpResult doGet(String url, int timeout, long startPos, HttpListener listener, KeyValuePair... header) {
        return doGet(url, null, timeout, startPos, listener, header);
    }

    public HttpResult doGet(String url, ArrayList<KeyValuePair> params, int timeout,
                            long startPos, HttpListener listener, KeyValuePair... header) {
        HttpExecutor.HttpRequestParams requestParams = new HttpExecutor.HttpRequestParams();
        requestParams.mParams = params;
        requestParams.mStartPos = startPos;
        requestParams.mTimeout = timeout;
        requestParams.isProxy = isProxy;
        if (null != header && header.length > 0) {
            HashMap<String, String> headers = new HashMap<String, String>();
            for (KeyValuePair keyValuePair : header) {
                headers.put(keyValuePair.getKey(), keyValuePair.getValue());
            }
            requestParams.mHeader = headers;
        }
        if (null == listener) {
            listener = new DataHttpListener();
        }
        return getHttpExecutor().doGet(url, requestParams, listener);
    }

    public HttpResult doPost(String url, ArrayList<KeyValuePair> params, HttpListener listener, KeyValuePair... header) {
        return doPost(url, null, params, null, listener, header);
    }

    public HttpResult doPost(String url, HttpExecutor.OutputStreamHandler osHandler, HttpListener listener, KeyValuePair... header) {
        return doPost(url, osHandler, null, null, listener, header);
    }

    public HttpResult doPost(String url, ArrayList<KeyValuePair> params, HashMap<String, HttpExecutor.ByteFile> byteFileMap,
                             HttpListener listener, KeyValuePair... header) {
        return doPost(url, null, params, byteFileMap, listener, header);
    }

    public HttpResult doPost(String url, HttpExecutor.OutputStreamHandler osHandler, ArrayList<KeyValuePair> params,
                             HashMap<String, HttpExecutor.ByteFile> byteFileMap, HttpListener listener, KeyValuePair... header) {
        HttpExecutor.HttpRequestParams requestParams = new HttpExecutor.HttpRequestParams();
        requestParams.mParams = params;
        requestParams.mOsHandler = osHandler;
        requestParams.isProxy = isProxy;
        if (null != header && header.length > 0) {
            HashMap<String, String> headers = new HashMap<String, String>();
            for (KeyValuePair keyValuePair : header) {
                headers.put(keyValuePair.getKey(), keyValuePair.getValue());
            }
            requestParams.mHeader = headers;
        }
        if (null != byteFileMap && byteFileMap.size() > 0) {
            requestParams.mByteFileMap = byteFileMap;
        }
        if (null == listener) {
            listener = new DataHttpListener();
        }
        return getHttpExecutor().doPost(url, requestParams, listener);
    }

}
