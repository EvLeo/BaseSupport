package com.leo.support.audio;

import com.leo.support.config.BaseConfig;
import com.leo.support.utils.BaseFileUtils;

import java.io.File;

/**
 * music缓存目录
 * Created by leo on 2017/6/5.
 */

public class MusicDir {

    public static File getMusicDir() {
        return BaseFileUtils.getDir(BaseConfig.getConfig().getAppRootDir(), "music");
    }
}
