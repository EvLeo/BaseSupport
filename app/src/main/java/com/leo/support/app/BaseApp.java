package com.leo.support.app;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.leo.support.app.activity.bean.BaseUserInfo;
import com.leo.support.log.LogUtil;
import com.leo.support.service.IServiceManager;
import com.leo.support.service.ServiceProvider;
import com.leo.support.utils.ArrayUtil;
import com.leo.support.utils.BroadcastUtils;
import com.leo.support.utils.ProcessUtils;

/**
 * done
 * Created by LiuYu on 2017/4/26.
 */
public class BaseApp extends Application {

    private static Context _mAppContext;
    private static Context _mAppContextTemp;

    private static BaseUserInfo mBaseUserInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        _mAppContextTemp = this;
        if (isProcessValid()) {
            _mAppContext = this;
            initApp();
        }
    }

    public static Context getAppContext() {
        if (_mAppContext != null)
            return _mAppContext;
        else
            return _mAppContextTemp;
    }

    public static BaseUserInfo getmUserInfo() {
        return mBaseUserInfo;
    }

    public static boolean isLogin() {
        return getmUserInfo() != null;
    }

    public void onLogin(Activity activity, BaseUserInfo userInfo) {
        BaseApp.mBaseUserInfo = userInfo;
    }

    public void onLogOut(Activity activity) {
    }

    public void initApp() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastUtils.registerGlobalReceiver(mBroadcastReceiver, intentFilter);
    }

    public void exitApp() {
        BroadcastUtils.unRegisterGlobalReceiver(mBroadcastReceiver);
    }

    public boolean isProcessValid() {
        String[] processNames = getValidProcessNames();
        if (ArrayUtil.isEmpty(processNames)) {
            return true;
        }
        String processName = ProcessUtils.getProcessName(this);
        for (String pName : processNames) {
            if (pName.equals(processName)) {
                return true;
            }
        }
        return false;
    }

    public String[] getValidProcessNames() {
        return null;
    }

    @Override
    public Object getSystemService(String name) {
        IServiceManager manager = ServiceProvider.getServiceProvider().getServiceManager();
        if (manager != null) {
            Object service = manager.getService(name);
            if (service != null) {
                return service;
            }
        }
        return super.getSystemService(name);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                onNetworkChange();
            }
        }
    };

    private void onNetworkChange() {
        LogUtil.v("BaseApp", "onNetworkChange");
    }
}
