package com.pw.box.tool;

import android.os.Bundle;
import android.os.SystemClock;

import com.pw.box.BuildConfig;
import com.pw.box.cache.Cache;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.accounts.SetLockFragment;
import com.pw.box.utils.L;

/**
 * 判断是否需要跳转到图案解锁的工具类，如果app切换到后台一定时间，则需要解锁后才能使用
 * Created by Administrator on 2017/3/23.
 */

public class UnLock {
    public static final long NEED_UNLOCK_TIME = BuildConfig.DEBUG ? 6000 : 60000;

    public static long unlockTime = 0;

    public static void unLockIfNeeded(ContainerActivity activity) {

        L.get().d("unLockIfNeeded" + new Exception());
        if (isUnlocked()) { // 距离上次解锁时间比较短
            unlockOk();
            return;
        }

        if (Cache.get().getPatternUtil().havePattern()) {
            Bundle arg = new Bundle();
            // arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_SET);
            arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_UNLOCK);
            // ContainerActivity.go(activity, SetLockFragment.class, arg);
            ContainerActivity.go(activity, SetLockFragment.class, arg);
        }
    }

    public static void unlockOk() {
        unlockTime = SystemClock.elapsedRealtime();
    }

    public static void feedDog() {
        L.get().d("feedDog" + new Exception());

        if (isUnlocked()) { // 距离上次解锁时间比较短
            unlockOk();
        }
    }

    public static boolean isUnlocked() {
        return SystemClock.elapsedRealtime() - unlockTime < NEED_UNLOCK_TIME;
    }
}
