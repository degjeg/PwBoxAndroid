package com.pw.box.tool

import android.content.Context
import android.os.SystemClock
import android.widget.Toast
import com.pw.box.R

/**
 * 再点一次退出程序，
 * 再点一次干某事
 * Created by danger on 2018/1/7.
 */
object DoubleTabHelper {
    var lastClickTime = 0L

    fun pressAgainToDoSth(context: Context, tipMessage: Int, t: () -> Unit) =
            pressAgainToDoSth(context, context.getString(tipMessage), t)

    fun pressAgainToDoSth(context: Context, tipMessage: CharSequence, t: () -> Unit) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
            t()
        } else {
            lastClickTime = SystemClock.elapsedRealtime()
            Toast.makeText(context, tipMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun pressAgainToExit(context: Context) {
        pressAgainToDoSth(context, R.string.press_again_to_exit) {
            ActivityManager.exitApp()
        }
    }
}