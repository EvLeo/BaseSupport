package com.leo.support.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import com.leo.support.log.LogUtil;

import java.util.List;

/**
 * done
 * Created by LiuYu on 2017/4/26.
 */
public class ProcessUtils {

    public static String getProcessName(Context context) {
        return getProcessName(context, ProcessUtils.getPid());
    }

    public static int getPid() {
        return android.os.Process.myPid();
    }

    public static String getProcessName(Context context, int pID) {
        if (context == null) {
            return "";
        }

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "";
        }
        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        if (CollectionUtil.isEmpty(infos)) {
            return "";
        }

        for (RunningAppProcessInfo info : infos) {
            try {
                if (info.pid == pID) {
                    return info.processName;
                }
            } catch (Exception e) {
                LogUtil.e("ProcessUtils.getProcessName:", e.getMessage());
            }
        }
        return "";
    }


}
