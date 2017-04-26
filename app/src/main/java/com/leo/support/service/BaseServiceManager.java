package com.leo.support.service;

import java.util.HashMap;

/**
 * Created by LiuYu on 2017/4/18.
 */
public class BaseServiceManager implements IServiceManager {

    private HashMap<String, Object> mServices = new HashMap<>();

    @Override
    public Object getService(String serviceName) {
        return mServices.get(serviceName);
    }

    @Override
    public void releaseAll() {

    }

    @Override
    public void registService(String serviceName, Object service) {
        if (mServices.containsKey(serviceName)) {
            return;
        }
        mServices.put(serviceName, service);
    }

    @Override
    public void unRegistService(String serviceName) {
        mServices.remove(serviceName);
    }

    /**
     * 初始化框架服务
     */
    public void InitBaseServices() {

    }
}
