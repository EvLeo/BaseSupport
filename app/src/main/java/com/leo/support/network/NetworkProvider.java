package com.leo.support.network;

/**
 * done
 * Created by leo on 2017/6/9.
 */

public class NetworkProvider {

    private static NetworkProvider _instance = null;

    private NetworkSensor mNetWorkSensor = new DefaultNetworkSensor();

    private NetworkProvider() {
    }

    public static NetworkProvider getNetworkProvider() {
        if (null == _instance) {
            _instance = new NetworkProvider();
        }
        return _instance;
    }

    public NetworkSensor getNetworkSensor() {
        return mNetWorkSensor;
    }

    public void registNetworkSensor(NetworkSensor sensor) {
        this.mNetWorkSensor = sensor;
    }
}
