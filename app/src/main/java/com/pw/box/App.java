package com.pw.box;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.pw.box.cache.Constants;
import com.pw.box.tool.ActivityManager;
import com.pw.box.utils.L;
import com.pw.box.utils.PrefUtil;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;


/**
 * 程序的总app
 * Created by danger on 16/8/19.
 */
public class App extends MultiDexApplication {
    static final List<Reference<Activity>> activities = new ArrayList<>();
    public static Context context;
    private static long lastPressBackTime = 0;

    public static Context getContext() {
        return context;
    }

    public static void initTest() {
        if (BuildConfig.DEBUG) {
            Constants.PROXY_HOST = PrefUtil.getString(context, Constants.PREF_KEY_HOST_PROXY, Constants.PROXY_HOST);
            Constants.PROXY_PORT = PrefUtil.getInt(context, Constants.PREF_KEY_PORT_PROXY, Constants.PROXY_PORT);
        }
    }

    public static String getVersionName() {
        try {
            return App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ActivityManager.INSTANCE.init(this);
        context = this;
        initTest();

        if (L.D) L.get().d("App", "server is:" + Constants.PROXY_HOST + ":" + Constants.PROXY_PORT);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
