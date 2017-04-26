package com.leo.support.database;

/**
 * done
 * Created by LiuYu on 2017/4/20.
 */
public class DataBaseManager {

    private DataBaseOpenHelper dataBaseOpenHelper;

    public static DataBaseManager _instance = null;

    private DataBaseManager() {
    }

    public static DataBaseManager getDataBaseManager() {
        if (_instance == null) {
            _instance = new DataBaseManager();
        }
        return _instance;
    }

    public <T extends BaseTable<?>> T getTable(Class<T> table) {
        return dataBaseOpenHelper.getTable(table);
    }

    public void registDataBaseHelper(DataBaseOpenHelper helper) {
        if (helper != null) {
            helper.close();
        }
        this.dataBaseOpenHelper = helper;
    }

    public DataBaseOpenHelper getDefaultDBHelper() {
        return dataBaseOpenHelper;
    }

    public void clearDataBase() {
        if (dataBaseOpenHelper != null) {
            dataBaseOpenHelper.clearDataBase();
        }
    }

    public void releaseDBHelper() {
        if (dataBaseOpenHelper != null) {
            try {
                dataBaseOpenHelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataBaseOpenHelper = null;
        }
    }
}
