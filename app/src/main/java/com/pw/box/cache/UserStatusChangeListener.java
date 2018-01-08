package com.pw.box.cache;

/**
 * 用户登录信息变化的接口
 * Created by Administrator on 2017/3/1.
 */

public interface UserStatusChangeListener {
    void onUserLoginStart();

    void onUserLoginSuccess();

    void onUserLoginFail();

    void onUserSyncDataStart();

    void onUserSyncDataSuccess();

    void onUserSyncDataFail();

    void onConnectionClosed();
}
