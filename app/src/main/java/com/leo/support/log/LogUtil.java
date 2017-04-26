package com.leo.support.log;

import android.text.TextUtils;

/**
 * done
 * Created by LiuYu on 2017/4/24.
 */
public class LogUtil {

    public final static String DEFAULT_TAG = "LEO";

    private static Logger mLogger = Logger.getLogger("LeoApp.log");

    public static final String KEYWORD_LOG_2_FILE = "LeoApp2file";
    public static final String KEYWORD_LOG_2_SDCARD = "LeoApp2SDCard";

    public static void setDebug(boolean debug) {
        if (debug) {
            mLogger.traceOn();
        } else {
            mLogger.traceOff();
        }
    }

    public static boolean idDebug() {
        return mLogger.isTrace();
    }

    public static void setLevel(int level) {
        mLogger.setLevel(level);
    }

    public static void d(String tag, String msg) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.d(tag, msg);
        }
    }

    public static void d(Object tag, String msg) {
        if (mLogger != null && tag != null) {
            mLogger.d(tag.getClass().getSimpleName(), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.e(tag, msg);
        }
    }

    public static void e(String tag, Throwable tr) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.e(tag, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.e(tag, msg, tr);
        }
    }

    public static void e(Object tag, String msg) {
        if (mLogger != null && tag != null) {
            mLogger.e(tag.getClass().getSimpleName(), msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (mLogger != null && !TextUtils.isEmpty(tag)) {
            mLogger.v(tag, msg);
        }
    }

    //----------------------default------------------------

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void w(String msg) {
        w(DEFAULT_TAG, msg);
    }

    public static void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }

}
