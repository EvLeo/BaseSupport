package com.leo.support.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * done
 * Created by LiuYu on 2017/4/19.
 */
public abstract class DataBaseOpenHelper extends SQLiteOpenHelper {

    private HashMap<Class<? extends BaseTable<?>>, BaseTable<?>> mTables;

    public DataBaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mTables = new HashMap<>();
        initTables(this);
    }

    public abstract void initTables(DataBaseOpenHelper dbHelper);

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgradeImpl(db, oldVersion, newVersion);
    }

    private void onUpgradeImpl(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mTables != null) {
            for (Class<? extends BaseTable<?>> clazz : mTables.keySet()) {
                BaseTable<?> table = mTables.get(clazz);
                table.onUpgrade(db, oldVersion, newVersion);
            }
        }
    }

    private void createTables(SQLiteDatabase db) {
        if (mTables != null) {
            for (Class<? extends BaseTable<?>> clazz : mTables.keySet()) {
                BaseTable<?> table = mTables.get(clazz);
                String sql = table.getCreateSql();
                if (!TextUtils.isEmpty(sql)) {
                    db.execSQL(sql);
                }
            }
        }
    }

    public void clearDataBase() {
        if (mTables != null) {
            for (Class<? extends BaseTable<?>> clazz : mTables.keySet()) {
                BaseTable<?> table = mTables.get(clazz);
                table.deleteByCase(null, null);//Passing null will delete all rows
            }
        }
    }

    public void addTable(Class<? extends BaseTable<?>> clazz, BaseTable<?> table) {
        mTables.put(clazz, table);
    }

    public HashMap<Class<? extends BaseTable<?>>, BaseTable<?>> getTables() {
        return mTables;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseTable<?>> T getTable(Class<T> table) {
        return (T) getTables().get(table);
    }

}
