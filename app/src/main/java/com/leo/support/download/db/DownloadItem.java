package com.leo.support.download.db;

import com.leo.support.database.BaseItem;
import com.leo.support.download.Task;

import java.sql.Date;

/**
 * DONE
 * Created by leo on 2017/6/16.
 */

public class DownloadItem extends BaseItem {

    public String mTaskId;
    public String mSrcPath;
    public String mDestPath;
    public long mDownloaded;
    public long mTotalLen;
    public int mStatus = Task.STATUS_UNINIT;
    //下载类型
    public String mSourceType;
    //扩展
    public String mExt;
    public Date mAddDate;
}
