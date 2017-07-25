package com.leo.support.download;

import com.leo.support.bean.KeyValuePair;
import com.leo.support.network.HttpProvider;
import com.leo.support.network.HttpResult;
import com.leo.support.network.listener.RandomFileHttpListener;

/**
 * 下载核心done
 * Created by leo on 2017/6/19.
 */

public class Downloader {

    private static final int TIME_OUT = 30;

    private int mStartPos;

    private volatile boolean mCanceled = false;

    private DownloaderListener mDownloaderListener;

    public void setStartPos(int startPos) {
        this.mStartPos = startPos;
    }

    public void cancel() {
        mCanceled = true;
    }

    public void setDownloaderListener(DownloaderListener listener) {
        this.mDownloaderListener = listener;
    }

    public boolean startTask(String remoteUrl, String destPath) {
        mCanceled = false;
        HttpProvider provider = new HttpProvider();
        HttpResult result = provider.doGet(remoteUrl, TIME_OUT, mStartPos,
                new RandomFileHttpListener(destPath) {
                    private long downloadLen = 0;
                    private long contentLength = 0;

                    @Override
                    public boolean onReady(String url) {
                        if (mCanceled) {
                            return false;
                        }
                        notifyDownloadReady();
                        return super.onReady(url);
                    }

                    @Override
                    public boolean onStart(long startPos, long contentLength) {
                        if (mCanceled) {
                            return false;
                        }
                        this.downloadLen = startPos;
                        this.contentLength = contentLength;
                        notifyDownloadStarted(startPos, contentLength);
                        return super.onStart(startPos, contentLength);
                    }

                    @Override
                    public boolean onAdvance(byte[] buffer, int offset, int len) {
                        if (mCanceled) {
                            return false;
                        }
                        this.downloadLen += (len - offset);
                        notifyDownloadAdvance(downloadLen, contentLength);
                        return super.onAdvance(buffer, offset, len);
                    }

                    @Override
                    public boolean onComplete() {
                        return !mCanceled && super.onComplete();
                    }

                    @Override
                    public boolean onRelease() {
                        return !mCanceled && super.onRelease();
                    }
                },
                // 当前正在使用的tcp链接在请求处理完毕后会被断掉
                // 以后client再进行新的请求时就必须创建新的tcp链接了
                // 不使用长链接
                new KeyValuePair("Connection", "close"));
        if (result.isSuccess()) {
            notifyDownloadSuccess();
        } else {
            notifyDownloadError(result);
        }
        return result.isSuccess();
    }

    private void notifyDownloadReady() {
        if (null != mDownloaderListener) {
            mDownloaderListener.onDownloadReady(this);
        }
    }

    private void notifyDownloadStarted(long startPos, long contentLength) {
        if (null != mDownloaderListener) {
            mDownloaderListener.onDownloadStarted(this, startPos, contentLength);
        }
    }

    private void notifyDownloadAdvance(long downloadLen, long contentLength) {
        if (null != mDownloaderListener) {
            mDownloaderListener.onDownloadAdvance(this, downloadLen, contentLength);
        }
    }

    private void notifyDownloadSuccess() {
        if (null != mDownloaderListener) {
            mDownloaderListener.onDownloadSuccess(this);
        }
    }

    private void notifyDownloadError(HttpResult result) {
        if (null != mDownloaderListener) {
            mDownloaderListener.onDownloadError(this, result);
        }
    }

    interface DownloaderListener {
        void onDownloadReady(Downloader downloader);

        void onDownloadStarted(Downloader downloader, long startPos, long contentLength);

        void onDownloadAdvance(Downloader downloader, long downloadLen, long contentLength);

        void onDownloadSuccess(Downloader downloader);

        void onDownloadError(Downloader downloader, HttpResult result);
    }

}
