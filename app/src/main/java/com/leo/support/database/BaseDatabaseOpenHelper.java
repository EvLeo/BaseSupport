package com.leo.support.database;

import android.content.Context;

/**
 * Created by LiuYu on 2017/4/20.
 */
public abstract class BaseDatabaseOpenHelper extends DataBaseOpenHelper {
    public BaseDatabaseOpenHelper(Context context, String name, int version) {
        super(context, name, version);
    }

    @Override
    public void initTables(DataBaseOpenHelper dbHelper) {
        initTablesImpl(dbHelper);
    }

    public abstract void initTablesImpl(DataBaseOpenHelper dbHelper);
}
