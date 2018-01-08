package com.pw.box.tool;

import com.pw.box.App;
import com.tencent.stat.StatService;

import java.util.Properties;

/**
 * mat/友盟统计工具类
 * Created by danger on 2017/1/14.
 */

public class StatTool {
    public static final String EVENT_REGISTER = "reg";
    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_ADD_DATA = "add_data";
    public static final String EVENT_DEL_DATA = "del_data";
    public static final String EVENT_EDIT_DATA = "edit_data";
    public static final String EVENT_AD = "ad";

    public static void trackEvent(String id, int code) {
        Properties prop = new Properties();
        prop.setProperty("code", String.valueOf(code));
        StatService.trackCustomKVEvent(App.getContext(), id, prop);
    }

    public static void trackAdEvent(String adid) {
        Properties prop = new Properties();
        prop.setProperty("adid", adid);

        StatService.trackCustomKVEvent(App.getContext(), EVENT_AD, prop);
    }
}
