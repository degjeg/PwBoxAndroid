package com.pw.box.tool

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.system.Os
import java.lang.ref.WeakReference

/**
 * Created by danger on 2018/1/7.
 */
object ActivityManager : Application.ActivityLifecycleCallbacks {
    val activities = mutableListOf<WeakReference<Activity>>()



    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.removeAll {
            it.get() == null || it.get() == activity
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(WeakReference(activity))
    }


    fun exitApp() {
        activities.forEach {
            it.get()?.finish()
        }
        activities.clear()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.kill(Process.myPid(), 0)
            } catch (e: Exception) {
                // e.printStackTrace();
            }

        }
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }
}