package com.pw.box.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.pw.box.App;

import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;


/**
 * 网络 工具类<Br>
 * 内部已经封装了打印功能,只需要把DEBUG参数改为true即可<br>
 * 如果需要更换tag可以直接更改,默认为KEZHUANG
 *
 * @author ZC
 */
public class NetWorkUtils {
    /**
     * Log 输出标签
     */
    public final static String TAG = "NetWorkUtils";


    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        /*ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // LogUtil.d(context, TAG, context.getString(com.mll.sdk.R.string.avalible_net), true);
                    return true;
                }
            }
        }*/
        // LogUtil.i(context, TAG, context.getString(com.mll.sdk.R.string.avalible_not_net), true);
        return true;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null || cm.getActiveNetworkInfo() == null) {
            // LogUtil.d(context, TAG, context.getString(com.mll.sdk.R.string.avalible_not_net), true);
            return false;
        }
        boolean isWifi = cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        // if (isWifi)
        // 	LogUtil.i(context, TAG, context.getString(com.mll.sdk.R.string.wifi_net), true);
        // else
        // 	LogUtil.i(context, TAG, context.getString(com.mll.sdk.R.string.not_wifi_net), true);
        return isWifi;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    public static int getCurrentNetType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                return info.getType();
            }
        }

        return -1;
    }

    public static String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) App.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            // 获取32位整型IP地址
            int ipAddress = wifiInfo.getIpAddress();

            // 返回整型地址转换成“*.*.*.*”地址
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
