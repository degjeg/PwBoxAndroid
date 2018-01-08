package com.pw.box.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Android shareprefrences工具类
 * Created by danger on 16/9/25.
 */

public class PrefUtil {

    private static final String TAG = PrefUtil.class.getSimpleName();
    private static final String CANT_GET_PREF = "Context is null, cannot read preference: ";
    private static final String CANT_WRITE_PREF = "Context is null, cannot write preference: ";
    private static final String PREF_SETTINGS = "PREF_SETTINGS";


    public static boolean getBool(Context ctx, String key, boolean defValue) {

        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            return settings.getBoolean(key, defValue);
        } else {
            Log.e(TAG, CANT_GET_PREF + key);
        }

        return defValue;
    }

    static public void setBool(Context ctx, String key, boolean value) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value);

            editor.apply();
        } else {
            Log.e(TAG, CANT_WRITE_PREF + key);
        }
    }

    public static int getInt(Context ctx, String key, int defValue) {

        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            return settings.getInt(key, defValue);
        } else {
            Log.e(TAG, CANT_GET_PREF + key);
        }

        return defValue;
    }

    static public void setInt(Context ctx, String key, int value) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(key, value);
            editor.apply();
        } else {
            Log.e(TAG, CANT_WRITE_PREF + key);
        }
    }

    public static float getFloat(Context ctx, String key, float defValue) {

        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            return settings.getFloat(key, defValue);
        } else {
            Log.e(TAG, CANT_GET_PREF + key);
        }

        return defValue;
    }

    static public void setFloat(Context ctx, String key, float value) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(key, value);
            editor.apply();
        } else {
            Log.e(TAG, CANT_WRITE_PREF + key);
        }
    }

    public static long getLong(Context ctx, String key, long defValue) {

        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            return settings.getLong(key, defValue);
        } else {
            Log.e(TAG, CANT_GET_PREF + key);
        }

        return defValue;
    }

    static public void setLong(Context ctx, String key, long value) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(key, value);
            editor.apply();
        } else {
            Log.e(TAG, CANT_WRITE_PREF + key);
        }
    }

    public static String getString(Context ctx, String key, String defValue) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            return settings.getString(key, defValue);
        } else {
            Log.e(TAG, CANT_GET_PREF + key);
        }

        return defValue;
    }

    static public void setString(Context ctx, String key, String value) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            editor.apply();
        } else {
            Log.e(TAG, CANT_WRITE_PREF + key);
        }
    }

    /**
     * Clears all the preferences set with this class
     */
    static public void clearAllPreferences(Context ctx) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
        } else {
            Log.e(TAG, "Content is null, cannot clear preferences");
        }
    }

    /**
     * Clears a single preference
     */
    static public void clearSinglePreference(Context ctx, String preferenceName) {
        if (ctx != null) {
            SharedPreferences settings = ctx.getSharedPreferences(PREF_SETTINGS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(preferenceName).apply();
        } else {
            Log.e(TAG, CANT_GET_PREF);
        }
    }
}
