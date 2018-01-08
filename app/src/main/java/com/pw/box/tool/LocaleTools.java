package com.pw.box.tool;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;

import com.pw.box.R;
import com.pw.box.cache.Constants;
import com.pw.box.utils.L;
import com.pw.box.utils.PrefUtil;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 语言切换工具类
 * Created by danger on 2017/1/11.
 */
public class LocaleTools {

    final static String prefKey = Constants.PREF_KEY_LAN;
    final static String SPLITOR = "✺✺✺";

    final static Locale[] locales = {
            null,
            Locale.SIMPLIFIED_CHINESE,
            Locale.ENGLISH
    };
    private static boolean hasInit = false;

    public static void init(Context context) {
        if (hasInit) return;
        hasInit = true;

        Locale locale = getSavedLocale(context);
        if (locale == null) return;

        setLocale(context, locale);
    }

    @Nullable
    private static Locale getSavedLocale(Context context) {
        String ls = PrefUtil.getString(context, prefKey, "");
        if (ls == null || !ls.contains(SPLITOR)) {
            return null;
        }
        String[] lss = ls.split(SPLITOR);
        String lang = lss[0];
        String country = lss.length > 1 ? lss[1] : "";
        Locale locale = new Locale(lang, country);
        return locale;
    }

    public static void showSelectLanguageDialog(final Activity activity) {
        Locale locale = getSavedLocale(activity);
        int fcs = 0;
        if (locale != null) {
            for (int i = 1; i < locales.length; i++) {
                if (locales[i].equals(locale)) {
                    fcs = i;
                    break;
                }
            }
        }

        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setTitle(R.string.language);
        b.setSingleChoiceItems(
                new CharSequence[]{
                        activity.getString(R.string.follow_system),
                        "简体中文",
                        "English"
                },

                fcs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setLocale(activity, locales[which]);
                        saveSelectedLocale(activity, locales[which]);
                        dialog.dismiss();

                        activity.finish();
                    }
                });

        b.create().show();
    }

    public static void saveSelectedLocale(Context context, Locale locale) {

        if (locale == null) {
            PrefUtil.setString(context, prefKey, "");
            return;
        }
        String l = new StringBuilder()
                .append(locale.getLanguage())
                .append(SPLITOR)
                .append(locale.getCountry())
                .toString();
        PrefUtil.setString(context, prefKey, l);

        setLocale(context, locale);
    }

    public static void setLocale(Context context, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (L.E) L.get().e("LocaleTools", "setLocale:" + locale);
        // 方法1
        try {
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration config = resources.getConfiguration();
            // 应用用户选择语言
            config.locale = locale;
            resources.updateConfiguration(config, dm);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        // 方法2
        IActivityManager iActMag = ActivityManagerNative.getDefault();
        try {
            Configuration config = iActMag.getConfiguration();
            config.locale = locale;
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            iActMag.updateConfiguration(config);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        // 方法3
        try {
            Object objIActMag, objActMagNative;
            Class clzIActMag = Class.forName("android.app.IActivityManager");
            Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
            // IActivityManager iActMag = ActivityManagerNative.getDefault();
            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            // Configuration config = iActMag.getConfiguration();
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
            config.locale = locale;
            // iActMag.updateConfiguration(config);
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            Class[] clzParams = {Configuration.class};
            Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod(
                    "updateConfiguration", clzParams);
            mtdIActMag$updateConfiguration.invoke(objIActMag, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
