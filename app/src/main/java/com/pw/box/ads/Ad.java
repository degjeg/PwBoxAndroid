package com.pw.box.ads;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pw.box.BuildConfig;
import com.pw.box.tool.StatTool;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.util.Locale;

/**
 * 腾讯广告的工具类
 * Created by Administrator on 2017/3/23.
 */

public class Ad {

    public static final String APPID = "1105863342";

    public static final String AD_SPLASH = "7080429153972538";
    public static final String AD_SUPPORT = "4030728143696634";
    public static final String AD_VIEW_DATA = "5000125183896665";
    public static final String AD_CHKVER = "7030529123399749";

    private static final String SKIP_TEXT = "点击跳过 %d";

    // 展示插屏广告
    public static void showInterstitial(Activity activity) {
        if (BuildConfig.DEBUG) return;

        StatTool.trackAdEvent(AD_CHKVER);
        final InterstitialAD iad = new InterstitialAD(activity, APPID, AD_CHKVER);
        iad.setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(AdError adError) {
                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + adError);
            }

            @Override
            public void onADReceive() {
                Log.i("AD_DEMO", "onADReceive");
                iad.show();
            }
        });
        iad.loadAD();
    }

    public static void showBanner(Activity activity, ViewGroup container) {
        if (BuildConfig.DEBUG) return;
        StatTool.trackAdEvent(AD_VIEW_DATA);

        BannerView bv = new BannerView(activity, ADSize.BANNER, APPID, AD_VIEW_DATA);
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bv.setRefresh(5);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError adError) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + adError);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });

        container.addView(bv);
        bv.loadAD();
    }

    public static void showSplash(Activity activity, ViewGroup adContainer, final TextView skipView, final GoNextListener goNextListener) {
        if (BuildConfig.DEBUG) return;
        SplashADListener splashADListener = new SplashADListener() {
            @Override
            public void onADPresent() {
                Log.i("AD_DEMO", "SplashADPresent");
                // splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
            }

            @Override
            public void onADClicked() {
                Log.i("AD_DEMO", "SplashADClicked");
            }

            /**
             * 倒计时回调，返回广告还将被展示的剩余时间。
             * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
             *
             * @param millisUntilFinished 剩余毫秒数
             */
            @Override
            public void onADTick(long millisUntilFinished) {
                Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
                skipView.setText(String.format(Locale.getDefault(), SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
            }

            @Override
            public void onADDismissed() {
                Log.i("AD_DEMO", "SplashADDismissed");
                // next();
                if (goNextListener != null) goNextListener.goNext();
            }

            @Override
            public void onNoAD(AdError adError) {
                Log.i("AD_DEMO", "LoadSplashADFail, eCode=" + adError);
                /* 如果加载广告失败，则直接跳转 */
                // this.startActivity(new Intent(this, DemoListActivity.class));
                // this.finish();
                if (goNextListener != null) goNextListener.goNext();
            }
        };
        SplashAD splashAD = new SplashAD(activity, adContainer, skipView, APPID, AD_SPLASH, splashADListener, 0);
        StatTool.trackAdEvent(AD_SPLASH);

        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goNextListener != null) goNextListener.goNext();
            }
        });

    }

    public interface GoNextListener {
        void goNext();
    }
}
