package com.leo.support.network;

import android.content.Context;
import android.net.ConnectivityManager;

import com.leo.support.bean.KeyValuePair;

import java.util.List;

/**
 * done
 * Created by leo on 2017/6/8.
 */

public interface NetworkSensor {

    public abstract String rebuildUrl(String url);

    public abstract List<KeyValuePair> getCommonHeaders(String url, boolean isProxy);

    public abstract HttpExecutor.ProxyHost getProxyHost(String url, boolean isProxy);

    public abstract void updateFlowRate(long len);

    public boolean isNetworkAvailable();

    public ConnectivityManager getConnectivityManager(Context context);

}
