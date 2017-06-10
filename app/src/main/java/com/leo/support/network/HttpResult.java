package com.leo.support.network;

import com.leo.support.network.listener.DataHttpListener;

/**
 * done
 * Created by leo on 2017/6/9.
 */

public class HttpResult {

    public String mUrl;
    public String mIp;
    public int mStatusCode = -1;
    public long mStartPos = -1;
    public long mContentLength;
    public int mErrorCode = HttpError.SUCCESS;
    public boolean mIsGzip = false;
    public boolean mIsTrunked = false;

    public boolean isSuccess(){
        return mErrorCode == HttpError.SUCCESS;
    }

    public long mApTs = 0;
    public long mReqTs = 0;
    public long mReadTs = 0;

    public boolean isChanceled() {
        return mErrorCode == HttpError.ERROR_CANCEL_BEGIN
                || mErrorCode == HttpError.ERROR_CANCEL_ADVANCE
                || mErrorCode == HttpError.ERROR_CANCEL_READY;
    }

    public String getCancelReason(){
        String str;
        switch (mErrorCode) {
            case HttpError.ERROR_CANCEL_BEGIN:
                str = "开始下载时";
                break;
            case HttpError.ERROR_CANCEL_ADVANCE:
                str = "正在读取时";
                break;
            case HttpError.ERROR_CANCEL_READY:
                str = "准备开始时";
                break;
            default:
                str = "未知";
                break;
        }
        return str;
    }

    public HttpListener mHttpListener;

    public String getResult() {
        if (null != mHttpListener && mHttpListener instanceof DataHttpListener) {
            DataHttpListener dataHttpListener = (DataHttpListener) mHttpListener;

            if (!isSuccess() || null == dataHttpListener.getData()) {
                return null;
            }

            try {
                return new String(dataHttpListener.getData(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
