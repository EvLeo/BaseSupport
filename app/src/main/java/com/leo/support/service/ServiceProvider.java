package com.leo.support.service;

/**
 * done
 * Created by LiuYu on 2017/4/18.
 */
public class ServiceProvider {
    private static ServiceProvider _instance = null;

    private IServiceManager mServiceManager = null;

    private ServiceProvider() {
    }

    public static ServiceProvider getServiceProvider() {
        if (_instance == null) {
            _instance = new ServiceProvider();
        }
        return _instance;
    }

    public void registServiceManager(IServiceManager serviceManager) {
        this.mServiceManager = serviceManager;
    }

    public IServiceManager getServiceManager() {
        return mServiceManager;
    }
}
