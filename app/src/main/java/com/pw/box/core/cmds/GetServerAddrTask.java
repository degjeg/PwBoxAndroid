package com.pw.box.core.cmds;

import android.content.Context;

import com.pw.box.App;
import com.pw.box.bean.protobuf.GetHostRes;
import com.pw.box.cache.Constants;
import com.pw.box.core.Cm;
import com.pw.box.core.Net;
import com.pw.box.utils.PrefUtil;
import com.squareup.wire.Message;

/**
 * 取服务器地址的工具类
 * Created by danger on 16/10/23.
 */

public class GetServerAddrTask {
    private static final String TAG = "";

    public static boolean haveTask = false;
    public static boolean successed = false;

    Net.NetHandler<GetHostRes> getHostHandler = new Net.NetHandler<GetHostRes>() {
        @Override
        public void onSuccess(int cmd, Message req, GetHostRes response) {
            // if(L.E) L.get().e(TAG, "getHost Result:" + response);
            if (response != null && response.host != null) {
                PrefUtil.setLong(App.getContext(), Constants.PREF_KEY_GOT_HOST_TIME, System.currentTimeMillis());

                Context ctx = App.getContext();
                PrefUtil.setString(ctx, Constants.PREF_KEY_HOST, response.host);
                PrefUtil.setInt(ctx, Constants.PREF_KEY_PORT, response.port);
                successed = true;
                Cm.get().init(response.host, response.port);
            } else {
                onFail(cmd, req, -1, null);
            }
            haveTask = false;
        }

        @Override
        public void onFail(int cmd, Message req, int code, Throwable e) {
            // if (TextUtils.isEmpty(Cm.get().getHost())) {
            //     // Toast.makeText(SplashActivity.this, "无法连接服务器,请检查网络后再试", Toast.LENGTH_SHORT).show();
            // }
            haveTask = false;
        }
    };

    public void run() {
        haveTask = true;
        Net net = new Net(CmdIds.GET_HOST, null, getHostHandler, Net.NOTIFY_TO_UITOTHISTHREAD);
        net.run();
    }

    public void enqueue() {
        haveTask = true;
        Net net = new Net(CmdIds.GET_HOST, null, getHostHandler);
        net.execute();
    }
}
