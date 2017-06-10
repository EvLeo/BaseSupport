package com.leo.support.network.listener;

import com.leo.support.network.HttpListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by leo on 2017/6/10.
 */

public class DataHttpListener implements HttpListener {
    private ByteArrayOutputStream mByteArrayOutputStream;
    private byte mBytedatas[];

    public byte[] getData() {
        return mBytedatas;
    }


    @Override
    public boolean onReady(String url) {
        return false;
    }

    @Override
    public boolean onStart(long startPos, long contentLength) throws Throwable {
        return false;
    }

    @Override
    public boolean onAdvance(byte[] buffer, int offset, int len) throws Throwable {
        return false;
    }

    @Override
    public boolean onComplete() throws Throwable {
        return false;
    }

    @Override
    public void onError(int statusCode) throws Throwable {

    }

    @Override
    public boolean onRelease() throws Throwable {
        return false;
    }

    @Override
    public boolean onResponse(InputStream is, OutputStream os, int statusCode, String mineType, String encoding, long contentLength, boolean repeatable, boolean isTrunk) {
        return false;
    }
}
