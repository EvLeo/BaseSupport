package com.leo.support.bean;

import android.text.TextUtils;

/**
 * done
 * Created by leo on 2017/6/8.
 */

public class KeyValuePair {

    private String mKey;
    private String mValue;

    public KeyValuePair() {
    }

    public KeyValuePair(String key, String value) {
        this.mKey = key;
        this.mValue = value;
    }

    public String getKey() {
        if (TextUtils.isEmpty(mKey)) {
            mKey = "";
        }
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getValue() {
        if (TextUtils.isEmpty(mValue)) {
            mValue = "";
        }
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

}
