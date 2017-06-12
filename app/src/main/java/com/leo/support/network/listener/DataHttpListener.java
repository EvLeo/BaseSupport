package com.leo.support.network.listener;

import com.leo.support.network.HttpListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * done
 * Created by leo on 2017/6/10.
 */

public class DataHttpListener implements HttpListener {
    private ByteArrayOutputStream mByteArrayOutputStream;
    private byte mByteData[];

    public byte[] getData() {
        return mByteData;
    }


    @Override
    public boolean onReady(String url) {
        return true;
    }

    @Override
    public boolean onStart(long startPos, long contentLength) throws Throwable {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        return true;
    }

    @Override
    public boolean onAdvance(byte[] buffer, int offset, int len) throws Throwable {
        if (null != mByteArrayOutputStream) {
            mByteArrayOutputStream.write(buffer, offset, len);
        }
        return true;
    }

    @Override
    public boolean onComplete() throws Throwable {
        if (null != mByteArrayOutputStream) {
            mByteData = mByteArrayOutputStream.toByteArray();
        }
        return true;
    }

    @Override
    public void onError(int statusCode) throws Throwable {
        if (null != mByteArrayOutputStream) {
            try {
                mByteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mByteArrayOutputStream = null;
        }
    }

    @Override
    public boolean onRelease() throws Throwable {
        return true;
    }

    @Override
    public boolean onResponse(InputStream is, OutputStream os, int statusCode,
                              String mineType, String encoding, long contentLength,
                              boolean repeatable, boolean isTrunk) {
        return false;
    }
}
