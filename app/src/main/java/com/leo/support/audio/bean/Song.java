package com.leo.support.audio.bean;

import android.text.TextUtils;

import com.leo.support.audio.MusicDir;
import com.leo.support.security.MD5Util;

import java.io.File;
import java.io.Serializable;

/**
 * done
 * Created by leo on 2017/6/5.
 */

public class Song implements Serializable {

    private static final long serialVersionUID = 4855284578291073536L;
    private String mUrl;
    private String mLocalPath;
    private boolean mIsOnline;

    public Song(boolean isOnline, String url, String localPath){
        this.mIsOnline = isOnline;
        this.mUrl = url;
        this.mLocalPath = localPath;
    }

    public File getLocalFile() {
        if (TextUtils.isEmpty(mLocalPath)) {
            return new File(MusicDir.getMusicDir(), MD5Util.encode(mUrl) + ".mp3");
        }
        return new File(mLocalPath);
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isOnline() {
        return mIsOnline;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            if (null != mUrl) {
                return mUrl.equals(((Song) o).mUrl);
            }
        }
        return super.equals(o);
    }
}
