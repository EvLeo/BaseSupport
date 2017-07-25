package com.leo.support.download.task;

import com.leo.support.download.Task;
import com.leo.support.download.db.DownloadItem;

/**
 * done
 * Created by leo on 2017/6/19.
 */

public class TaskFactory {
    private static TaskFactory _instance;
    private TaskFactory(){}

    public static TaskFactory getTaskFactory() {
        if (null == _instance) {
            _instance = new TaskFactory();
        }
        return _instance;
    }

    public Task buildTask(DownloadItem item) {
        String sourceType = item.mSourceType;
        Task task = null;
        if (null != mDownloadTaskBuilder) {
            task = mDownloadTaskBuilder.buildTask(sourceType, item);
            if (null != task) {
                return task;
            }
        }
        return UrlTask.createUrlTask(item);
    }

    private DownloadTaskBuilder mDownloadTaskBuilder;
    public void registDownloadTaskBuilder(DownloadTaskBuilder builder) {
        this.mDownloadTaskBuilder = builder;
    }

    interface DownloadTaskBuilder {
        Task buildTask(String sourceType, DownloadItem downloadItem);
    }
}
