package com.leo.support.service;

/**
 * done
 * Created by LiuYu on 2017/4/18.
 */
public interface IServiceManager {

    public abstract Object getService(String serviceName);

    public abstract void releaseAll();

    public abstract void registService(String serviceName, Object service);

    public abstract void unRegistService(String serviceName);
}
