package com.leo.support.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.leo.support.log.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LiuYu on 2017/4/19.
 */
public abstract class BaseTable<T> implements BaseColumns {

    public static final String SCHEMA = "content://";

    private String mTableName;
    private SQLiteOpenHelper mSqliteOpenHelper;

    public BaseTable(String tableName, SQLiteOpenHelper sqLiteOpenHelper) {
        this.mTableName = tableName;
        this.mSqliteOpenHelper = sqLiteOpenHelper;
    }

    public abstract String getCreateSql();

    public abstract ContentValues getContentValues(T item);

    public abstract T getItemFromCursor(Cursor cursor);

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public String getTableName() {
        return mTableName;
    }

    public List<T> queryAll() {
        return queryByCase(null, null, null);
    }

    public List<T> queryByCase(String where, String args[], String orderby) {
        List<T> items = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return null;
            }
            items = new ArrayList<>();
            cursor = db.query(getTableName(), null, where, args, null, null, orderby);
            while (cursor.moveToNext()) {
                items.add(getItemFromCursor(cursor));
            }
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;

    }

    public T querySingleLineByCase(String where, String args[], String orderby) {
        List<T> items = queryByCase(where, args, orderby);
        if (items != null && items.size() > 0) {
            return items.get(0);
        }
        return null;
    }

    public List<T> rawQuery(String sql) {
        List<T> items = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return null;
            }
            items = new ArrayList<>();
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                items.add(getItemFromCursor(cursor));
            }
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }

    public int getCount(String where, String args[]) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return 0;
            }
            cursor = db.query(getTableName(), null, where, args, null, null, null);
            return cursor.getCount();
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public long insert(T item) {
        long result = -1;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return -1;
            }
            result = db.insert(getTableName(), null, getContentValues(item));
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return result;
    }

    public void insert(List<T> items) {
        SQLiteDatabase db = getSQLiteDatabase();
        try {
            if (db == null) {
                return;
            }
            db.beginTransaction();
            for (T item : items) {
                db.insert(getTableName(), null, getContentValues(item));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public int deleteByCase(String where, String args[]) {
        int count = -1;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return -1;
            }
            count = db.delete(getTableName(), where, args);
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return count;
    }

    public int updateByCase(T item, String where, String args[]) {
        int count = -1;
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return -1;
            }
            ContentValues values = getContentValues(item);
            count = db.update(getTableName(), values, where, args);
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return count;
    }


    public T queryById(String id) {
        List<T> items = queryByCase(_ID + "=" + id, null, null);
        if (items != null && items.size() > 0) {
            return items.get(0);
        }
        return null;
    }

    @SuppressLint("UseValueOf")
    @SuppressWarnings({"hiding", "unchecked"})
    public <S> S getValue(Cursor cursor, String columnName, Class<S> t) {
        int index = cursor.getColumnIndex(columnName);
        if (String.class.getName().equals(t.getName())) {
            if (index >= 0) {
                return (S) cursor.getString(index);
            }
            return null;
        } else if (Integer.class.getName().equals(t.getName())) {
            if (index >= 0) {
                return (S) new Integer(cursor.getInt(index));
            }
            return (S) new Integer(0);
        } else if (Long.class.getName().equals(t.getName())) {
            if (index >= 0) {
                return (S) new Long(cursor.getLong(index));
            }
            return (S) new Long(0);
        } else if (Float.class.getName().equals(t.getName())) {
            if (index >= 0) {
                return (S) new Float(cursor.getFloat(index));
            }
            return (S) new Float(0);
        } else if (Double.class.getName().equals(t.getName())) {
            if (index >= 0) {
                return (S) new Double(cursor.getDouble(index));
            }
            return (S) new Double(0);
        } else if (Date.class.getName().equals(t.getName())) {
            if (index >= 0) {
                return (S) new Date(cursor.getLong(index));
            }
            return (S) new Date(System.currentTimeMillis());
        }
        return null;
    }


    public SQLiteDatabase getSQLiteDatabase() {
        if (mSqliteOpenHelper != null) {
            return mSqliteOpenHelper.getWritableDatabase();
        }
        SQLiteOpenHelper helper = DataBaseManager.getDataBaseManager().getDefaultDBHelper();
        if (helper == null) {
            return null;
        }
        return helper.getWritableDatabase();
    }

    public void execSQL(String sql) {
        try {
            SQLiteDatabase db = getSQLiteDatabase();
            if (db == null) {
                return;
            }
            db.execSQL(sql);
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
    }

    public void dropColumn(SQLiteDatabase db, String columnName) {
        try {
            if (db == null) {
                return;
            }
            db.execSQL("ALTER TABLE " + getTableName() + " DROP COLUMN " + columnName + ";");
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
    }

    public void alterColumn(SQLiteDatabase db, String columnName, String newType) {
        try {
            if (db == null) {
                return;
            }
            db.execSQL("ALTER TABLE " + getTableName() + " ALTER COLUMN " + columnName + " " + newType + ";");
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
    }

    public void addColumn(SQLiteDatabase db, String columnName, String dataType) {
        try {
            if (db == null) {
                return;
            }
            db.execSQL("ALTER TABLE " + getTableName() + " ADD COLUMN " + columnName + " " + dataType + ";");
        } catch (SQLException e) {
            String str = e.getMessage();
            if (str.contains("duplicate column name")) {
                // 如果是这个异常信息，表示数据库中已经有这个字段了，这是正常的，不会对数据有异常行为
                LogUtil.e(getTableName(), str);
            } else {
                LogUtil.e(getTableName(), e);
            }
        }
    }

    public void dropTable() {
        execSQL("DROP TABLE " + getTableName() + ";");
    }

    public static Uri getNotifyUri(String tableName) {
        return Uri.parse(SCHEMA + "com.leo.provider.providers.update_" + tableName + "/" + tableName);
    }

    public void notifyChange(Uri uri) {
//        BaseApp.getAppContext().getContentResolver().notifyChange(uri, null);
    }

    public void notifyDataChange() {
        notifyChange(getNotifyUri(getTableName()));
    }


}
