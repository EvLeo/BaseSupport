package com.leo.support.download;

import android.graphics.drawable.VectorDrawable;

import com.leo.support.database.DataBaseManager;
import com.leo.support.download.db.DownloadTable;
import com.leo.support.log.LogUtil;
import com.leo.support.security.MD5Util;
import com.leo.support.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * done
 * Created by leo on 2017/6/16.
 */

public class DownloadManager {
    public static final String TAG = "DownloadManager";

    private static DownloadManager _instance = null;

    private Vector<Task> downloadTaskList = new Vector<>();
    private Vector<Task> finishedList = new Vector<>();
    private Vector<Task> unFinishedList = new Vector<>();

    private List<Task.TaskListener> mTaskListeners = null;

    private volatile boolean mIsRunning = false;
    private volatile boolean mCanceled = true;

    private DownloadManager() {
        reloadTasks();
    }

    public static DownloadManager getDownloadManager() {
        if (null == _instance) {
            _instance = new DownloadManager();
        }
        return _instance;
    }

    private void reloadTasks() {

    }

    public void start() {
        mCanceled = false;
        scheduleNext();
    }

    public void cancelAll() {
        mCanceled = true;
        if (null != downloadTaskList && downloadTaskList.size() > 0) {
            for (Task task : downloadTaskList) {
                if (null != task) {
                    task.pause();
                }
            }
            unFinishedList.addAll(0, downloadTaskList);
        }
    }

    public void addTaskListener(Task.TaskListener listener) {
        if (null == mTaskListeners) {
            mTaskListeners = new ArrayList<>();
        }
        mTaskListeners.add(listener);
    }

    public void removeTaskListener(Task.TaskListener listener) {
        if (null != mTaskListeners) {
            mTaskListeners.remove(listener);
        }
    }

    public String buildTaskId(String url) {
        return MD5Util.encode(url);
    }

    public List<Task> getTaskList() {
        return unFinishedList;
    }

    private void scheduleNext() {
        if (mIsRunning) {return;}
        mIsRunning = true;
        if (unFinishedList.isEmpty()) {
            mIsRunning = false;
            return;
        }
        Task task = unFinishedList.remove(0);
        if (null != task && downloadTaskList.isEmpty()) {
            downloadTaskList.add(task);
            task.setTaskListener(mTaskListener);
            new Thread(task).start();
        }
    }

    public Task getTaskById(String taskId) {
        for (Task task : downloadTaskList) {
            if (null != task && taskId.equals(task.getTaskId())) {
                return task;
            }
        }
        for (Task task : unFinishedList) {
            if (null != task && taskId.equals(task.getTaskId())) {
                return task;
            }
        }
        for (Task task : finishedList) {
            if (null != task && taskId.equals(task.getTaskId())) {
                return task;
            }
        }
        return null;
    }

    private void addTask(Task task) {
        if (null == task) {
            return;
        }
        unFinishedList.add(task);
        Collections.sort(unFinishedList, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                return rhs.getPriority() - lhs.getPriority();
            }
        });
    }

    private void removeTask(Task task) {
        if (null != task) {
            task.setInvalid();
        }
    }

    private void clearTask(Task task) {
        DownloadTable table = DataBaseManager.getDataBaseManager().getTable(DownloadTable.class);
        if (null != table && null != task) {
            table.removeDownload(task.getTaskId());
        }
        if (null != task) {
            task.setTaskListener(null);
            unFinishedList.remove(task);
        }
    }

    public void removeTaskById(String taskId) {
        Task task = getTaskById(taskId);
        if (null != task) {
            removeTask(task);
        }
    }

    private Task.TaskListener mTaskListener = new Task.TaskListener() {
        @Override
        public void onReady(Task task) {
            LogUtil.v(TAG, "Task ready, taskId: " + task.getTaskId());
            if (null != mTaskListeners) {
                for (Task.TaskListener listener : mTaskListeners) {
                    if (null != listener) {
                        listener.onReady(task);
                    }
                }
            }
        }

        @Override
        public void onStart(Task task, long startPos, long totalLen) {
            LogUtil.v(TAG, "Task started, taskId: " + task.getTaskId() + " , startPos: " + startPos + " , totalLen: " + totalLen);
            if (null != mTaskListeners) {
                for (Task.TaskListener listener : mTaskListeners) {
                    if (null != listener) {
                        listener.onStart(task, startPos, totalLen);
                    }
                }
            }
        }

        @Override
        public void onProgress(Task task, long progress, long totalLen) {
            LogUtil.v(TAG, "Task Progress, taskId: " + task.getTaskId() + " , progress: " + progress + ", totalLen: " + totalLen);
            if (null != mTaskListeners) {
                for (Task.TaskListener listener : mTaskListeners) {
                    if (null != listener) {
                        listener.onProgress(task, progress, totalLen);
                    }
                }
            }
        }

        @Override
        public void onComplete(Task task, int reason) {
            if (task.isValid()) {
                List<Task>  delTasks = new ArrayList<>();
                for (Task taskItem : unFinishedList) {
                    if (task.getTaskId().equals(taskItem.getTaskId())) {
                        delTasks.add(taskItem);
                        notifyTaskComplete(taskItem, reason);
                    }
                }
                unFinishedList.removeAll(delTasks);
                downloadTaskList.remove(task);

                finishedList.add(task);
                finishedList.addAll(delTasks);
                notifyTaskComplete(task, reason);
            } else {
                clearTask(task);
            }
            mIsRunning = false;
            if (mCanceled) {
                return;
            }
            scheduleNext();
        }

        private void notifyTaskComplete(Task task, int reason) {
            LogUtil.v(TAG, "Task complete, taskId: " + task.getTaskId() + " , complete reason: " + reason);
            if (null != mTaskListeners) {
                for (Task.TaskListener listener : mTaskListeners) {
                    if (null != listener) {
                        listener.onComplete(task, reason);
                    }
                }
            }
        }
    };


}
