package com.leo.support.network.listener;

import android.text.TextUtils;

import com.leo.support.network.HttpListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * 任意位置读写
 * RandomAccessFile: seek(pos) 定位位置
 * Created by leo on 2017/6/20.
 */

public class RandomFileHttpListener implements HttpListener {

    private File mFile = null;
    private RandomAccessFile mRandomAccessFile;

    public RandomFileHttpListener(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            mFile = file;
        }
    }

    @Override
    public boolean onReady(String url) {
        try {
            mRandomAccessFile = new RandomAccessFile(mFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean onStart(long startPos, long contentLength) {
        if (null != mRandomAccessFile) {
            try {
                mRandomAccessFile.seek(startPos);
                if (startPos == 0) {
                    mRandomAccessFile.setLength(contentLength);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onAdvance(byte[] buffer, int offset, int len) {
        if (null != mRandomAccessFile) {
            try {
                mRandomAccessFile.write(buffer, offset, len);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onComplete() {
        return true;
    }

    @Override
    public void onError(int statusCode) {
        onComplete();
    }

    @Override
    public boolean onRelease() {
        if (null != mRandomAccessFile) {
            try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onResponse(InputStream is, OutputStream os, int statusCode,
                              String mineType, String encoding, long contentLength,
                              boolean repeatable, boolean isTrunk) {
        return false;
    }
}
