package com.leo.support.download.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.leo.support.database.BaseTable;
import com.leo.support.download.Task;
import com.leo.support.log.LogUtil;

import java.sql.Date;
import java.util.List;

/**
 * done
 * Created by leo on 2017/6/16.
 */

public class DownloadTable extends BaseTable<DownloadItem> {

    private static final String TABLE_NAME = "download";

    private static final String TASKID = "taskid";
    private static final String SRC_PATH = "srcpath";
    private static final String DEST_PATH = "destpath";
    private static final String DOWNLOADED = "downloaded";
    private static final String TOTAL_LEN = "totallen";
    private static final String STATUS = "status";
    private static final String SOURCE_TYPE = "sourcetype";
    private static final String ADD_TIME = "addtime";
    private static final String EXT = "ext";

    public DownloadTable(SQLiteOpenHelper sqLiteOpenHelper) {
        super(TABLE_NAME, sqLiteOpenHelper);
    }

    @Override
    public String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TASKID + " TEXT,"
                + SRC_PATH + " TEXT,"
                + DEST_PATH + " TEXT,"
                + DOWNLOADED + " INTEGER,"
                + TOTAL_LEN + " INTEGER,"
                + STATUS + " INTEGER,"
                + SOURCE_TYPE + " TEXT,"
                + ADD_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + EXT + " TEXT);";
    }

    @Override
    public ContentValues getContentValues(DownloadItem item) {
        ContentValues values = new ContentValues();
        values.put(TASKID, item.mTaskId);
        values.put(SRC_PATH, item.mSrcPath);
        values.put(DEST_PATH, item.mDestPath);
        values.put(DOWNLOADED, item.mDownloaded);
        values.put(TOTAL_LEN, item.mTotalLen);
        values.put(STATUS, item.mStatus);
        values.put(SOURCE_TYPE, item.mSourceType);
        values.put(EXT, item.mExt);
        return values;
    }

    @Override
    public DownloadItem getItemFromCursor(Cursor cursor) {
        DownloadItem item = new DownloadItem();
        item.mTaskId = getValue(cursor, TASKID, String.class);
        item.mSrcPath = getValue(cursor, SRC_PATH, String.class);
        item.mDestPath = getValue(cursor, DEST_PATH, String.class);
        item.mDownloaded = getValue(cursor, DOWNLOADED, Long.class);
        item.mTotalLen = getValue(cursor, TOTAL_LEN, Long.class);
        item.mStatus = getValue(cursor, STATUS, Integer.class);
        item.mSourceType = getValue(cursor, SOURCE_TYPE, String.class);
        item.mExt = getValue(cursor, EXT, String.class);
        item.mAddDate = getValue(cursor, ADD_TIME, Date.class);
        return item;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable();
        execSQL(getCreateSql());
    }

    public int updateProgress(String taskId, long progress, long totalLen) {
        int count = -1;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (null == db) {
                return -1;
            }
            ContentValues values = new ContentValues();
            values.put(DOWNLOADED, progress);
            values.put(TOTAL_LEN, totalLen);
            count = db.update(getTableName(), values, TASKID + "=?", new String[]{taskId});
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return count;
    }

    public int updateStatus(String taskId, int status) {
        int count = -1;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (null == db) {
                return -1;
            }
            ContentValues values = new ContentValues();
            values.put(STATUS, status);
            count = db.update(getTableName(), values, TASKID + "=?", new String[]{taskId});
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return count;
    }

    public DownloadItem queryDownloadBySrcPath(String srcPath) {
        return querySingleLineByCase(SRC_PATH + "=?", new String[]{srcPath}, ADD_TIME + " asc");
    }

    public DownloadItem queryDownloadByTaskId(String taskId) {
        return querySingleLineByCase(TASKID + "=?", new String[]{taskId}, ADD_TIME + " asc");
    }

    public List<DownloadItem> queryUnFinishedDownload() {
        return queryByCase(STATUS + "<" + Task.STATUS_COMPLETE, null, ADD_TIME + " asc");
    }

    public List<DownloadItem> queryFinishedDownload() {
        return queryByCase(STATUS + "=" + Task.STATUS_COMPLETE, null, ADD_TIME + " asc");
    }

    public void removeDownload(String taskId) {
        deleteByCase(TASKID + "=?", new String[]{taskId});
    }

}
