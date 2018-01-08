//package com.pw.box.cache;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//
//import com.pw.box.R;
//import com.pw.box.ui.base.ContainerActivity;
//import com.pw.box.ui.fragments.accounts.SetLockFragment;
//
///**
// * 对程序的前台或者后台运行进行计时
// * Created by danger on 2016/11/13.
// */
//public class Counter implements Handler.Callback {
//
//    HandlerThread workThread;
//    Handler handler;
//
//    int currentState = 0; // 1 running 0 not running
//    int runbackgroudTime = 0;
//
//    private static Counter counter;
//
//    public static Counter get() {
//        if (counter == null) {
//            synchronized (Counter.class) {
//                if (counter == null) {
//                    counter = new Counter();
//                }
//            }
//        }
//        return counter;
//    }
//
//    private Counter() {
//        this.workThread = new HandlerThread("count");
//        workThread.start();
//        handler = new Handler(workThread.getLooper(), this);
//        handler.sendEmptyMessageDelayed(1, 1000);
//    }
//
//    @Override
//    public boolean handleMessage(Message message) {
//        handler.removeCallbacksAndMessages(null);
//
//        if (currentState == 1) {
//            runbackgroudTime = 0;
//        } else {
//            runbackgroudTime += 1000;
//        }
//
//        // if(L.E) L.get().e("Counter", "runbackgroudTime:" + runbackgroudTime);
//        handler.sendEmptyMessageDelayed(1, 1000);
//        return false;
//    }
//
//    public void onResume(Activity activity) {
//        // currentState = 1;
//        if (activity instanceof ContainerActivity
//                && runbackgroudTime > 30000
//                && Cache.get().getPatternUtil().havePattern()
//                ) {
//            ContainerActivity containerActivity = (ContainerActivity) activity;
//            Fragment f = containerActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//            if (!(f instanceof SetLockFragment)) {
//                Bundle arg = new Bundle();
//                arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_UNLOCK);
//                ((ContainerActivity) activity).showFragment(SetLockFragment.class, arg);
//                return;
//            }
//        }
//        currentState = 1;
//    }
//
//    public void onPause() {
//        currentState = 0;
//    }
//
//    public boolean needUnLock() {
//        return Cache.get().getPatternUtil().havePattern() && runbackgroudTime > 3000;
//    }
//
//    public void setRunning() {
//        runbackgroudTime = 0;
//        currentState = 1;
//    }
//
//    public int getRunbackgroudTime() {
//        return runbackgroudTime;
//    }
//}
