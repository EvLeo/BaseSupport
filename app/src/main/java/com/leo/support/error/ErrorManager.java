package com.leo.support.error;

import android.text.TextUtils;

/**
 * done
 * Created by LiuYu on 2017/4/19.
 */
public class ErrorManager {

    private static ErrorManager _instance = null;

    private ErrorMap errorMap;

    private ErrorManager(){}

    public static ErrorManager getErrorManager() {
        if (_instance == null) {
            _instance = new ErrorManager();
        }
        return _instance;
    }

    public void registErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    public String getErrorHint(String errorCode, String desc) {
        String hint = errorMap.getErrorHint(errorCode, desc);
        if (TextUtils.isEmpty(hint)) {
            return "网络连接异常，请稍候再试!";
        }
        return hint;
    }

}
