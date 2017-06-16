package com.leo.support.network.listener;

/**
 * 可关闭的http监听
 * Created by leo on 2017/6/16.
 */

public class CancelableListener extends DataHttpListener {

    private boolean isCancel = false;

    public void cancel() {
        isCancel = true;
    }

    @Override
    public boolean onReady(String url) {
        return !isCancel && super.onReady(url);
    }

    @Override
    public boolean onStart(long startPos, long contentLength) {
        return !isCancel && super.onStart(startPos, contentLength);
    }

    @Override
    public boolean onAdvance(byte[] buffer, int offset, int len) {
        return !isCancel && super.onAdvance(buffer, offset, len);
    }

    @Override
    public boolean onComplete() {
        return !isCancel && super.onComplete();
    }

    @Override
    public boolean onRelease() {
        return !isCancel && super.onRelease();
    }
}
