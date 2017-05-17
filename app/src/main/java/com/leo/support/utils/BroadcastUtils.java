package com.leo.support.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.leo.support.app.BaseApp;
import com.leo.support.log.LogUtil;

/**
 * done
 * Created by leo on 2017/5/17.
 */

public class BroadcastUtils {

    private static final String TAG = "BroadcastUtils";

    /**
     * 发送全局广播
     *
     * @param intent
     */
    public static void sendGlobalBroadcast(Intent intent) {
        try {
            if (null == intent) {
                return;
            }
            BaseApp.getAppContext().sendBroadcast(intent);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 发顺序广播
     *
     * @param intent
     */
    public static void sendGlobalOrderedBroadcast(Intent intent) {
        try {
            if (null == intent) {
                return;
            }
            BaseApp.getAppContext().sendOrderedBroadcast(intent, null);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 注册全局广播
     *
     * @param receiver
     * @param filter
     */
    public static void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            BaseApp.getAppContext().registerReceiver(receiver, filter);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 注销全局广播
     *
     * @param receiver
     */
    public static void unRegisterGlobalReceiver(BroadcastReceiver receiver) {
        try {
            BaseApp.getAppContext().unregisterReceiver(receiver);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 发送本地广播
     *
     * @param intent
     */
    public static void sendLocalBroadcast(Intent intent) {
        try {
            LocalBroadcastManager.getInstance(BaseApp.getAppContext()).sendBroadcast(intent);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 注册本地广播
     *
     * @param receiver
     * @param filter
     */
    public static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            LocalBroadcastManager.getInstance(BaseApp.getAppContext()).registerReceiver(receiver, filter);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 注销本地广播
     *
     * @param receiver
     */
    public static void unRegisterLocalReceiver(BroadcastReceiver receiver) {
        try {
            LocalBroadcastManager.getInstance(BaseApp.getAppContext()).unregisterReceiver(receiver);
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
        }
    }
}
