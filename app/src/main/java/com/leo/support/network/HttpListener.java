package com.leo.support.network;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * HttpProvider下载监听器
 * <p>
 * 调用顺序如下：
 * onReady->onStart->onAdvance->onComplete->onRelease
 * 状态码不正确的情况：
 * onReady->onError->onRelease
 * <p>
 * Created by leo on 2017/6/7.
 */

public interface HttpListener {

    public boolean onReady(String url);

    public boolean onStart(long startPos, long contentLength) throws Throwable;

    public boolean onAdvance(byte[] buffer, int offset, int len) throws Throwable;

    public boolean onComplete() throws Throwable;

    public void onError(int statusCode) throws Throwable;

    public boolean onRelease() throws Throwable;

    public boolean onResponse(InputStream is, OutputStream os, int statusCode,
                              String mineType, String encoding, long contentLength,
                              boolean repeatable, boolean isTrunk);
}
