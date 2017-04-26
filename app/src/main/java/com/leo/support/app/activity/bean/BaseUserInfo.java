package com.leo.support.app.activity.bean;

import java.io.Serializable;

/**
 * done
 * Created by LiuYu on 2017/4/26.
 */
public class BaseUserInfo implements Serializable {

    private static final long serialVersionUID = -96756370863960796L;

    public String userId;

    public String userName;

    public String token;

    public BaseUserInfo() {
    }

    public BaseUserInfo(String userId, String userName, String token) {
        this.userId = userId;
        this.userName = userName;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
