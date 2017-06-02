package com.leo.support.config;

import java.io.File;

/**
 * Created by leo on 2017/6/2.
 */

public class BaseConfig {
    public static final int APP_LEO = 1;

    private static BaseConfig _instance = null;

    private File mImageCacheDir;

    private BaseConfig() {}

    public static BaseConfig getConfig() {
        if (_instance == null)
            _instance = new BaseConfig();
        return _instance;
    }

    public BaseConfig setAppRootDir(File dir) {
        this.mImageCacheDir = dir;
        return this;
    }

    public File getAppRootDir() {
        return mImageCacheDir;
    }

}
