package com.leo.support.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.leo.support.app.BaseApp;
import com.leo.support.bean.KeyValuePair;
import com.leo.support.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认网络感应器
 * Created by leo on 2017/6/9.
 */

public class DefaultNetworkSensor implements NetworkSensor {

    private static final String TAG = "DefaultNetworkSensor";

    private ConnectivityManager mConnectivityManager;

    public DefaultNetworkSensor() {
    }

    @Override
    public String rebuildUrl(String url) {
        return url.replace(" ", "%20").replace("\"", "%22")
                .replace("#", "%23").replace("(", "%28")
                .replace(")", "%29").replace("+", "%2B")
                .replace(",", "%2C").replace(";", "%3B")
                .replace("<", "%3C").replace(">", "%3E")
                .replace("@", "%40").replace("\\", "%5C")
                .replace("|", "%7C");
    }

    @Override
    public List<KeyValuePair> getCommonHeaders(String url, boolean isProxy) {
        return new ArrayList<KeyValuePair>();
    }

    @Override
    public HttpExecutor.ProxyHost getProxyHost(String url, boolean isProxy) {
        return getProxy(url, isProxy);
    }

    private HttpExecutor.ProxyHost getProxy(String url, boolean isProxy) {
        return null;
    }

    @Override
    public void updateFlowRate(long len) {
    }

    @Override
    public boolean isNetworkAvailable() {
        return isNetworkAvailable(BaseApp.getAppContext());
    }

    private boolean isNetworkAvailable(Context context) {
        if (null == context) {
            return false;
        }

        try {
            if (null == getConnectivityManager(context)) {
                LogUtil.d(TAG, ">>>>>couldn't get connectivity manager");
            } else {
                ConnectivityManager connectivityManager = getConnectivityManager(context);
                if (null == connectivityManager) {
                    LogUtil.d(TAG, "+++couldn't get connectivity manager");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Network[] networks = connectivityManager.getAllNetworks();
                        NetworkInfo networkInfo;
                        if (null != networks) {
                            for (Network network : networks) {
                                networkInfo = connectivityManager.getNetworkInfo(network);
                                if (NetworkInfo.State.CONNECTED.equals(networkInfo.getState())) {
                                    LogUtil.d(TAG, ">>>>>network " + networkInfo.getTypeName() + " is available");
                                    return true;
                                }
                            }
                        }
                    } else {
                        NetworkInfo[] ifs = connectivityManager.getAllNetworkInfo();
                        if (ifs != null) {
                            for (NetworkInfo info : ifs) {
                                if (NetworkInfo.State.CONNECTED.equals(info.getState())) {
                                    LogUtil.d(TAG, ">>>>>network " + info.getTypeName() + " is available");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, ">>>>>network is not available");
        return false;
    }

    @Override
    public ConnectivityManager getConnectivityManager(Context context) {
        if (null == mConnectivityManager) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return mConnectivityManager;
    }

    public void release() {
    }


}
