package com.pw.box.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.pw.box.R;
import com.pw.box.tool.LocaleTools;
import com.pw.box.ui.activity.SplashFragment;


/**

 */
public class ContainerActivity extends AppCompatActivity {

    private static final String EXTRA_KEY_REQUEST_CDOE = "request_code_";
    protected int minFragments = 1;
    private int fragmentRequestCode;
    private Integer fragmentResultCode;
    private Intent fragmentResultData;

    public static Intent getIntent(Context activity, Class<? extends Fragment> f, Bundle args) {
        return getIntent(activity, f.getName(), args);
    }

    public static Intent getIntent(Context activity, String f, Bundle args) {
        Intent intent = new Intent(activity, ContainerActivity.class);
        intent.putExtra("fname", f);
        intent.putExtra("args", args);
        return intent;
    }

    public static void go(Context activity, Class<? extends Fragment> f, Bundle args) {
        go(activity, f.getName(), args);
    }

    public static void go(Context activity, String fname, Bundle args) {
        activity.startActivity(getIntent(activity, fname, args));
    }

    public static void goClearTask(Context activity, Class<? extends Fragment> f, Bundle args) {
        goClearTask(activity, f.getName(), args);
    }

    public static void goClearTask(Context activity, String fname, Bundle args) {
        Intent intent = getIntent(activity, fname, args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        LocaleTools.init(this);
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_empty_container);

        onNewIntent(getIntent());

        // IntentFilter intentFilter = new IntentFilter();
        // intentFilter.addAction(getPackageName() + ".newversion");
        // registerReceiver(receiver, intentFilter);
    }

    protected void init() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Counter.get().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Counter.get().onResume(this);
        /*// 已经登录
        if (Cache.get().getStopTime() > 0
                && SystemClock.elapsedRealtime() - Cache.get().getStopTime() > 30000
                && Cache.get().getPatternUtil().havePattern()
                ) {
            Bundle arg = new Bundle();
            arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_UNLOCK);
            showFragment(SetLockFragment.class, arg);
        }*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra("fname")) {
            showFragment(intent.getStringExtra("fname"), intent.getBundleExtra("args"));
        } else {
            showFragment(SplashFragment.class, null);
        }
    }


    public boolean isDestroyed() {
        if (isFinishing()) {
            return true;
        }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && super.isDestroyed();
    }

    private void showFragment(Class<? extends Fragment> f, Bundle args) {
        showFragment(f, args, null);
    }

    private void showFragment(Class<? extends Fragment> f, Bundle args, Integer reqCode) {
        showFragment(f.getName(), args, reqCode);
    }

    private void showFragment(String fname, Bundle args) {
        showFragment(Fragment.instantiate(this, fname, args), null);
    }

    private void showFragment(String fname, Bundle args, Integer reqCode) {
        showFragment(Fragment.instantiate(this, fname, args), reqCode);
    }

    private void showFragment(Fragment fragment, Integer reqCode) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (reqCode != null) {
            Bundle args = fragment.getArguments();
            if (args == null) {
                args = new Bundle();
                fragment.setArguments(args);
            }
            args.putInt(EXTRA_KEY_REQUEST_CDOE, reqCode);
        }

        String tag = fragment.getClass().getName();

        // Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        // if (currentFragment != null && !replaceMode) {
        //     fragmentTransaction.addToBackStack(currentFragment.getClass().getName());
        // }
        fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof BaseFragment) {
            BaseFragment baseFragment = (BaseFragment) currentFragment;
            if (baseFragment.onBackPressed()) {
                return;
            }
        }

        finish();
    }
}
