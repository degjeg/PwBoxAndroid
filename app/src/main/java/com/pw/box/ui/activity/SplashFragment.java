package com.pw.box.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pw.box.App;
import com.pw.box.BuildConfig;
import com.pw.box.R;
import com.pw.box.ads.Ad;
import com.pw.box.bean.protobuf.CheckAppVerRequest;
import com.pw.box.bean.protobuf.CheckAppVerResponse;
import com.pw.box.cache.Cache;
import com.pw.box.cache.Constants;
import com.pw.box.core.Cm;
import com.pw.box.core.K;
import com.pw.box.core.Net;
import com.pw.box.core.ThreadPool;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.core.cmds.GetServerAddrTask;
import com.pw.box.tool.DoubleTabHelper;
import com.pw.box.tool.UnLock;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.base.DialogActivity;
import com.pw.box.ui.fragments.AppUpgradeDialogAdapter;
import com.pw.box.ui.fragments.accounts.LoginFragment;
import com.pw.box.ui.fragments.accounts.SetLockFragment;
import com.pw.box.ui.fragments.home.HomeFragment;
import com.pw.box.utils.L;
import com.pw.box.utils.PrefUtil;
import com.squareup.wire.Message;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.tencent.stat.StatService;

public class SplashFragment extends BaseFragment implements Runnable, Ad.GoNextListener {

    private static final java.lang.String TAG = "SplashActivity";
    private static final long SPLASH_DELAY = 1000;
    Handler handler;


    ContainerActivity activity;
    int step = 0;
    private AlertDialog updateDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        K.i(App.getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_splash, container, false);
        activity = (ContainerActivity) getActivity();
        handler = new Handler();

        // if (BuildConfig.DEBUG) {
        //     showFragment(GroupListFragment.class, null);
        //     return v;
        // }
        if (!GetServerAddrTask.successed) {
            ThreadPool.execute(this);
        } else {
            finishDelayed((int) SPLASH_DELAY);
        }

        // getHost();
        if (!Cache.checkVerSuccessed) checkVer();

        TextView tvSkip = v.findViewById(R.id.tv_skip);
        if (!BuildConfig.DEBUG) {
            Ad.showSplash(getActivity(), (ViewGroup) v.findViewById(R.id.ad_container), tvSkip, this);
        } else {
            step |= 2;
            step = 3;
            doGoNext();
        }

        return v; // super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!BuildConfig.DEBUG) initMtaConfig();
    }


    @Override
    public boolean onBackPressed() {
        DoubleTabHelper.INSTANCE.pressAgainToExit(getContext());
        return true;
    }

    void initMtaConfig() {
        StatConfig.setDebugEnable(false);
        // 根据情况，决定是否开启MTA对app未处理异常的捕获
        StatConfig.setAutoExceptionCaught(false);
        // 选择默认的上报策略
        StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);


        String appkey = "APKN3E8WN91A";
        // 初始化并启动MTA
        // 第三方SDK必须按以下代码初始化MTA，其中appkey为规定的格式或MTA分配的代码。
        // 其它普通的app可自行选择是否调用
        try {
            // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
            StatService.startStatService(getContext(), appkey,
                    com.tencent.stat.common.StatConstants.VERSION);
        } catch (MtaSDkException e) {
            // MTA初始化失败
            // if(L.E) logger.error("MTA start failed.");
            // if(L.E) logger.error("e");
        }
    }

    private void checkVer() {
        CheckAppVerRequest.Builder req = new CheckAppVerRequest.Builder();
        req.channel(BuildConfig.FLAVOR);

        PackageManager pm = activity.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(activity.getPackageName(), 0);
            req.ver(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Net(CmdIds.CHECK_VER, req.build(), new Net.NetHandler<CheckAppVerResponse>() {
            @Override
            public void onSuccess(int cmd, Message req, CheckAppVerResponse response) {

                Cache.checkVerSuccessed = true;

                if (response != null
                        && response.info != null
                        && !TextUtils.isEmpty(response.info.url)) {

                    Intent intent = DialogActivity.newIntent(activity, response.info, new AppUpgradeDialogAdapter());
                    activity.startActivity(intent);
                }
                if (L.E) L.get().e(TAG, "checkVer Result:" + response);
            }

            @Override
            public void onFail(int cmd, Message req, int code, Throwable e) {

            }
        }).execute();
    }

    @Override
    public void finishDelayed(int time) {
        // if(L.E) L.get().e(TAG, "finishDelayed", new Throwable());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                step |= 1;
                doGoNext();
            }
        }, time);
    }

    @Override
    public void run() {
        final long start = SystemClock.elapsedRealtime();

        Context ctx = activity;

        String host = PrefUtil.getString(ctx, Constants.PREF_KEY_HOST, BuildConfig.DEBUG ? null : Constants.PROXY_HOST);
        int port = PrefUtil.getInt(ctx, Constants.PREF_KEY_PORT, BuildConfig.DEBUG ? -1 : 1001);

        if (!TextUtils.isEmpty(host)) {
            Cache.get();
            Cm.get().init(host, port);
            // Cm.get().init("192.168.5.250", 9000);
            // Cm.get().connect(user.getAccount());
            long lastGetTime = PrefUtil.getLong(ctx, Constants.PREF_KEY_GOT_HOST_TIME, 0L);
            long needUpdateTime = BuildConfig.DEBUG ? -1 : 5 * 60000;
            if (Math.abs(System.currentTimeMillis() - lastGetTime) > needUpdateTime) {
                new GetServerAddrTask().enqueue();
            }
        } else {
            new GetServerAddrTask().run();
        }

        long used = SystemClock.elapsedRealtime() - start;
        long delay = used > SPLASH_DELAY ? 0 : SPLASH_DELAY - used;
        finishDelayed((int) delay);
    }

    private void doGoNext() {
        if (updateDialog != null) {
            return;
        }
        if (L.E) L.get().e(TAG, "doGoNext:" + step, new Exception());
        if (step != 3) {
            return;
        }


        step = 4;
        boolean isFirst = PrefUtil.getBool(getContext(), Constants.PREF_KEY_SHOW_WALKTHOUGH, true);

        // isFirst = 3 > 1;
        /*isFirst =false;
        if (isFirst) {
            PrefUtil.setBool(getContext(), Constants.PREF_KEY_SHOW_WALKTHOUGH, false);
            activity.finish();

            Intent intent = new Intent(activity, WalkThoughActivity.class);

            intent.putExtra("fname", WalkthoughFragment.class.getName());
            activity.startActivity(intent);
            // WalkThoughActivity.go(activity, WalkthoughFragment.class, null);
            // activity.showFragment(WalkthoughFragment.class, null);

        } else */
        if (UnLock.isUnlocked()
                && Cache.get().getLoginStatus() == Cache.LOGIN_STATUS_LOGGIN) {
            ContainerActivity.goClearTask(activity, HomeFragment.class, null);
        } else if (Cache.get().getPatternUtil().havePattern()) {
            Bundle arg = new Bundle();
            // arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_SET);
            arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_UNLOCK);
            // ContainerActivity.go(activity, SetLockFragment.class, arg);
            ContainerActivity.goClearTask(activity, SetLockFragment.class, arg);
        } else {
            ContainerActivity.goClearTask(activity, LoginFragment.class, null);
        }
    }

    @Override
    public void goNext() {
        step |= 2;
        doGoNext();
    }

    //    static {
    //        System.loadLibrary("core");
    //        if(L.E) L.get().e("xxxxx", Arrays.toString(K.K1));
    //        if(L.E) L.get().e("xxxxx", Arrays.toString(K.K2));
    //        if(L.E) L.get().e("xxxxx", K.R1);
    //        if(L.E) L.get().e("xxxxx", K.R2);
    //    }
}

