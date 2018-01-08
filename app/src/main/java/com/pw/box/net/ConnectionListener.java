package com.pw.box.net;

import com.pw.box.core.bean.Pack;

/**
 * 网络连接通知接口
 * Created by Administrator on 2017/3/1.
 */

public interface ConnectionListener {
    void onIdle();

    void onConnected();

    void onClosed(Exception e);

    void onReceive(Pack buffer);
}
