package com.leo.support.network;

/**
 * http错误码
 * Created by leo on 2017/6/8.
 */

public class HttpError {
    public static final int SUCCESS = 0;//下载成功
    public static final int ERROR_CANCEL_BEGIN = -1;//开始下载时Cancel
    public static final int ERROR_CANCEL_ADVANCE = -2;//下载时Cancel
    public static final int ERROR_STATUS_CODE = -3;//状态码不正确
    public static final int ERROR_UNKNOWN = -4;//由于其他异常导致的错误
    public static final int ERROR_CANCEL_READY = -5;//准备时Cancel
    public static final int ERROR_URL_EMPTY = -6;//URL为空
    public static final int ERROR_NO_AVAILABLE_NETWORK = -7;//没有可用网络
    public static final int ERROR_CANCEL_RESPONSE = -8;//response时Cancel
}
