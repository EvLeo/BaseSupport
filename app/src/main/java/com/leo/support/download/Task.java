package com.leo.support.download;

import android.text.TextUtils;

import com.leo.support.database.DataBaseManager;
import com.leo.support.download.db.DownloadItem;
import com.leo.support.download.db.DownloadTable;
import com.leo.support.log.LogUtil;
import com.leo.support.network.HttpError;
import com.leo.support.network.HttpResult;
import com.leo.support.network.NetworkProvider;

import java.util.UUID;

/**
 * done
 * Created by leo on 2017/6/16.
 */

public abstract class Task implements Downloader.DownloaderListener, Runnable {

    public static int PRIORITY_LOW = 1;
    public static int PRIORITY_MIDDLE = 2;
    public static int PRIORITY_HIGH = 3;

    public static final int STATUS_UNINIT = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_STARTED = 2;
    public static final int STATUS_PAUSE = 3;
    public static final int STATUS_ADVANCING = 4;
    public static final int STATUS_ERROR = 5;
    public static final int STATUS_COMPLETE = 6;

    private String mTaskId;
    private int mStartPos;
    private int mDownloaded;
    private int mTotalLen;
    private int mStatus = STATUS_UNINIT;
    private Downloader mDownloader;
    private TaskListener mTaskListener;
    private DownloadTable mDownloadTable;

    private boolean mValid = true;

    public Task() {
        this(UUID.randomUUID().toString());
    }

    public Task(String taskId) {
        this.mTaskId = taskId;
        mDownloadTable = DataBaseManager.getDataBaseManager().getTable(DownloadTable.class);
        mDownloader = new Downloader();
    }

    public Task(DownloadItem item) {
        this.mTaskId = item.mTaskId;
        this.mStartPos = (int) item.mDownloaded;
        this.mDownloaded = (int) item.mDownloaded;
        this.mStatus = item.mStatus;
        LogUtil.v(DownloadManager.TAG, "init downloaded: " + mDownloaded);
        mDownloadTable = DataBaseManager.getDataBaseManager().getTable(DownloadTable.class);
        mDownloader = new Downloader();
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void setStartPos(int startPos) {
        this.mStartPos = startPos;
    }

    public int getStartPos() {
        return mStartPos;
    }

    public void setTotalLen(int totalLen) {
        this.mTotalLen = totalLen;
    }

    public int getTotalLen() {
        return mTotalLen;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public int getProgress() {
        return mDownloaded;
    }

    public void notifyDownloaded() {
        if (null != mTaskListener) {
            mStatus = STATUS_COMPLETE;
            mTaskListener.onComplete(this, TaskListener.REASON_SUCCESS);
        }
    }

    public void setInvalid() {
        this.mValid = false;
        if (null != mDownloader) {
            mDownloader.cancel();
        }
    }

    public boolean isValid() {
        return mValid;
    }

    public void pause() {
        LogUtil.v(DownloadManager.TAG, "pause taskId: " + mTaskId);
        if (null != mDownloader) {
            mDownloader.cancel();
        }
    }


    public void setTaskListener(TaskListener listener) {
        this.mTaskListener = listener;
    }

    public TaskListener getTaskListener() {
        return mTaskListener;
    }

    public abstract String getRemoteUrl();

    public abstract String getDestFilePath();

    public abstract int getPriority();

    public abstract String getTaskType();


    @Override
    public void onDownloadReady(Downloader downloader) {
        LogUtil.v(DownloadManager.TAG, "onReady taskId: " + mTaskId);
        mStatus = STATUS_READY;
        if (null != mDownloadTable) {
            mDownloadTable.updateStatus(mTaskId, mStatus);
        }
        if (null != mTaskListener) {
            mTaskListener.onReady(this);
        }
    }

    @Override
    public void onDownloadStarted(Downloader downloader, long startPos, long contentLength) {
        LogUtil.v(DownloadManager.TAG, "onStarted taskId: " + mTaskId);
        mStatus = STATUS_STARTED;
        if (null != mDownloadTable) {
            mDownloadTable.updateStatus(mTaskId, mStatus);
        }
        this.mStartPos = (int) startPos;
        this.mDownloaded = (int) startPos;
        LogUtil.v(DownloadManager.TAG, "started downloaded: " + mDownloaded);
        if (mTotalLen == 0) {
            this.mTotalLen = (int) contentLength;
        }
        if (null != mTaskListener) {
            mTaskListener.onStart(this, startPos, mTotalLen);
        }
        if (null != mDownloadTable) {
            mDownloadTable.updateProgress(mTaskId, startPos, mTotalLen);
        }
    }

    @Override
    public void onDownloadAdvance(Downloader downloader, long downloadLen, long contentLength) {
        if (null != mDownloadTable && mStatus != STATUS_ADVANCING) {
            mDownloadTable.updateStatus(mTaskId, STATUS_ADVANCING);
            LogUtil.v(DownloadManager.TAG, "onAdvance taskId: " + mTaskId);
        }
        this.mStatus = STATUS_ADVANCING;
        this.mStartPos = (int) downloadLen;
        this.mDownloaded = (int) downloadLen;
        LogUtil.v(DownloadManager.TAG, "onAdvance downloaded: " + mDownloaded);

        if (null != mTaskListener) {
            mTaskListener.onProgress(this, downloadLen, mTotalLen);

            if (null != mDownloadTable && isInnerPercentChange()) {
                mDownloadTable.updateProgress(mTaskId, downloadLen, mTotalLen);
            }
        }
    }

    @Override
    public void onDownloadSuccess(Downloader downloader) {
        LogUtil.v(DownloadManager.TAG, "onComplete taskId: " + mTaskId);
        mStatus = STATUS_COMPLETE;
        if (null != mDownloadTable) {
            mDownloadTable.updateStatus(mTaskId, mStatus);
        }
        if (null != mTaskListener) {
            mTaskListener.onComplete(this, TaskListener.REASON_SUCCESS);
        }
    }

    @Override
    public void onDownloadError(Downloader downloader, HttpResult result) {
        if (null != mTaskListener) {
            int reason = TaskListener.REASON_SUCCESS;
            switch (result.mErrorCode) {
                case HttpError.ERROR_CANCEL_READY:
                case HttpError.ERROR_CANCEL_BEGIN:
                case HttpError.ERROR_CANCEL_ADVANCE:
                case HttpError.ERROR_CANCEL_RESPONSE:
                    mStatus = STATUS_PAUSE;
                    reason = TaskListener.REASON_CANCEL;
                    if (null != mDownloadTable) {
                        mDownloadTable.updateStatus(mTaskId, mStatus);
                    }
                    LogUtil.v(DownloadManager.TAG, "onPause taskId: " + mTaskId);
                    break;

                case HttpError.ERROR_STATUS_CODE:
                case HttpError.ERROR_UNKNOWN:
                case HttpError.ERROR_URL_EMPTY:
                case HttpError.ERROR_NO_AVAILABLE_NETWORK:
                    mStatus = STATUS_ERROR;
                    reason = TaskListener.REASON_NETWORK;
                    if (null != mDownloadTable) {
                        mDownloadTable.updateStatus(mTaskId, mStatus);
                    }
                    LogUtil.v(DownloadManager.TAG, "onError taskId: " + mTaskId);
                    break;

                case HttpError.SUCCESS:
                    reason = TaskListener.REASON_SUCCESS;
                    break;

                default:
                    break;

            }
            mTaskListener.onComplete(this, reason);
        }
    }

    @Override
    public void run() {
        startDownloadImpl();
    }

    private void startDownloadImpl() {
        if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
            if (null != mTaskListener) {
                mStatus = STATUS_ERROR;
                mTaskListener.onComplete(this, TaskListener.REASON_NETWORK);
            }
            return;
        }

        mDownloader.setStartPos(getStartPos());
        mDownloader.setDownloaderListener(this);
        String remoteUrl = getRemoteUrl();
        String destFilePath = getDestFilePath();
        if (TextUtils.isEmpty(remoteUrl)) {
            if (null != mTaskListener) {
                mStatus = STATUS_ERROR;
                mTaskListener.onComplete(this, TaskListener.REASON_EMPTY_URL);
            }
            return;
        }
        if (TextUtils.isEmpty(destFilePath)) {
            if (null != mTaskListener) {
                mStatus = STATUS_ERROR;
                mTaskListener.onComplete(this, TaskListener.REASON_EMPTY_LOCAL_PATH);
            }
            return;
        }
        mDownloader.startTask(remoteUrl, destFilePath);
    }

    interface TaskListener {
        int REASON_SUCCESS = 0;
        int REASON_NETWORK = 1;
        int REASON_CANCEL = 2;
        int REASON_EMPTY_URL = 3;
        int REASON_EMPTY_LOCAL_PATH = 4;

        void onReady(Task task);

        void onStart(Task task, long startPos, long totalLen);

        void onProgress(Task task, long progress, long totalLen);

        void onComplete(Task task, int reason);

    }

    private int mInnerPercent = -1;

    private boolean isInnerPercentChange() {
        if (mTotalLen != 0) {
            int percent = mDownloaded * 100 / mTotalLen;
            if (percent == mInnerPercent) {
                return false;
            } else {
                mInnerPercent = percent;
                return true;
            }
        }
        return false;
    }
}
