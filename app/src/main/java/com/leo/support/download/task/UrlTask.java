package com.leo.support.download.task;

import com.leo.support.download.Task;
import com.leo.support.download.db.DownloadItem;

/**
 * done
 * Created by leo on 2017/6/19.
 */

public class UrlTask extends Task {

    private static final String SOURCE_TYPE = "urlTask";

    private String remoteUrl;
    private String destFilePath;

    public UrlTask(DownloadItem item) {
        super(item);
    }

    public static UrlTask createUrlTask(DownloadItem item) {
        UrlTask task = new UrlTask(item);
        task.remoteUrl = item.mSrcPath;
        task.destFilePath = item.mDestPath;
        return task;
    }

    @Override
    public String getRemoteUrl() {
        return remoteUrl;
    }

    @Override
    public String getDestFilePath() {
        return destFilePath;
    }

    @Override
    public int getPriority() {
        return PRIORITY_MIDDLE;
    }

    @Override
    public String getTaskType() {
        return SOURCE_TYPE;
    }
}
