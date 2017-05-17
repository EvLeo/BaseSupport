package com.leo.support.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * done
 * Created by leo on 2017/5/17.
 */

public class VersionUtils {

    /**
     * 获取应用版本名
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != pi) {
            return pi.versionName;
        }
        return null;
    }

    /**
     * 获取应用版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != pi) {
            return pi.versionCode;
        }
        return -1;
    }

    /**
     * 获取应用名
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != pi && null != pi.applicationInfo
                && !TextUtils.isEmpty(pi.applicationInfo.name)) {
            return pi.applicationInfo.name;
        }
        return "";
    }

}
