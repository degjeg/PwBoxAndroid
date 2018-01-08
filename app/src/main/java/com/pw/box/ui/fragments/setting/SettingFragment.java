package com.pw.box.ui.fragments.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.App;
import com.pw.box.BuildConfig;
import com.pw.box.R;
import com.pw.box.ads.Ad;
import com.pw.box.bean.protobuf.AppUpdateInfo;
import com.pw.box.bean.protobuf.CheckAppVerRequest;
import com.pw.box.bean.protobuf.CheckAppVerResponse;
import com.pw.box.cache.Cache;
import com.pw.box.core.Cm;
import com.pw.box.core.Net;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.tool.LocaleTools;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.base.DialogActivity;
import com.pw.box.ui.fragments.AppUpgradeDialogAdapter;
import com.pw.box.ui.fragments.accounts.ChangePasswordFragment;
import com.pw.box.ui.fragments.accounts.LoginFragment;
import com.pw.box.ui.fragments.accounts.SetLockFragment;
import com.pw.box.ui.fragments.accounts.SetProtectionFragment;
import com.pw.box.utils.L;
import com.squareup.wire.Message;

import java.util.Arrays;

/**
 * 数据列表界面
 * Created by danger on 16/8/30.
 */
public class SettingFragment extends BaseFragment implements BaseRecyclerViewAdapterNew.OnItemClickListener<SettingsItem> {

    static final int SET_ITEM_ACCOUNT = 1;
    static final int SET_ITEM_LOCK = 2;
    static final int SET_ITEM_PASSWORD = 3; // 修改密码
    static final int SET_ITEM_PROTECT = 4; // 密码保护
    // TODO
    static final int SET_ITEM_PHONE = 5; // 手机
    // TODO
    static final int SET_ITEM_EMAIL = 6; // 邮箱
    static final int SET_ITEM_CLEAR_CACHE = 7; //
    static final int SET_ITEM_LOGOUT = 8; //
    static final int SET_ITEM_ABOUT = 9; //
    static final int SET_ITEM_CHECKUPDATE = 10; //
    static final int SET_ITEM_FEEDBACK = 11; //
    static final int SET_ITEM_HTTP = 12; //
    static final int SET_ITEM_HELP = 13;
    static final int SET_ITEM_VIEW_DESC = 14;
    static final int SET_LANGUAGE = 15;
    private static final java.lang.String TAG = "SettingFragment";
    private static final SettingsItem[] items = new SettingsItem[]{
            new SettingsItem(SET_ITEM_ACCOUNT, R.string.account, ""), // 屏幕锁
            new SettingsItem(SET_ITEM_LOCK, R.string.pattern_unlock, ""), // 屏幕锁
            new SettingsItem(SET_ITEM_PASSWORD, R.string.modify_password, ""), // 屏幕锁
            new SettingsItem(SET_ITEM_PROTECT, R.string.password_protection, ""), // 密码保护

            // new SettingsItem(SET_ITEM_PHONE, "手机", ""), //
            // new SettingsItem(SET_ITEM_EMAIL, "邮箱", ""), //
            // new SettingsItem(SET_ITEM_CLEAR_CACHE, "清除缓存", ""), //
            // new SettingsItem(SET_ITEM_ABOUT, "关于", App.getVersionName()), //
            new SettingsItem(SET_ITEM_FEEDBACK, R.string.feedback, ""), //
            new SettingsItem(SET_ITEM_HELP, R.string.help, ""), //
            // new SettingsItem(SET_ITEM_VIEW_DESC, R.string.view_introduction, ""), //
            new SettingsItem(SET_LANGUAGE, R.string.language, ""), //
            new SettingsItem(SET_ITEM_CHECKUPDATE, R.string.app_update, App.getVersionName()), //
            new SettingsItem(SET_ITEM_LOGOUT, R.string.logout, ""), //

            // new SettingsItem(SET_ITEM_HTTP, "局域网", ""), //
            // new SettingsItem(SET_ITEM_HTTP, "局域网", ""), //
    };
    ViewHolder holder;
    SettingsAdapter adapter;
    Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater.inflate(R.layout.fragment_settings, container, false));

        adapter = new SettingsAdapter(getContext());

        adapter.setData(Arrays.asList(items));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(this);
        // holder.recyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        holder.recyclerView.setAdapter(adapter);
        return holder.rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
    }


    private void showSetLanguageDialog() {
        LocaleTools.showSelectLanguageDialog(getActivity());
    }

    private void feedback() {
        ContainerActivity.go(activity, FeedbackFragment.class, null);
    }

    private void checkUpdate() {
        CheckAppVerRequest.Builder req = new CheckAppVerRequest.Builder();
        req.channel(BuildConfig.FLAVOR);

        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            req.ver(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        showProgressDialog(R.string.checking_update);
        new Net(CmdIds.CHECK_VER, req.build(), new Net.NetHandler<CheckAppVerResponse>() {
            @Override
            public void onSuccess(int cmd, Message req, CheckAppVerResponse response) {
                dismissDialog();

                if (response != null
                        && response.info != null
                        && !TextUtils.isEmpty(response.info.url)) {


                    AppUpdateInfo msg = response.info;

                    Intent intent = DialogActivity.newIntent(activity, msg, new AppUpgradeDialogAdapter());
                    activity.startActivity(intent);
                } else {
                    toast(R.string.no_new_version_found);

                    // 特殊平台-安智不允许弹出插屏广告
                    if (!BuildConfig.FLAVOR.equals("anzhi")) {
                        Ad.showInterstitial(getActivity());
                    }
                }
                if (L.E) L.get().e(TAG, "checkVer Result:" + response);
            }

            @Override
            public void onFail(int cmd, Message req, int code, Throwable e) {
                dismissDialog();
                toast(R.string.no_new_version_found);
            }
        }).execute();
    }

    private void onClickLogout() {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext());
        dlgBuilder.setTitle(R.string.tip);
        dlgBuilder.setMessage(R.string.message_sure_to_logout);
        dlgBuilder.setNegativeButton(R.string.misoperation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dlgBuilder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Cm.get().close();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
                Cache.get().clear();

                ContainerActivity.goClearTask(activity, LoginFragment.class, null);
            }
        });
        dlgBuilder.setCancelable(false);
        dlgBuilder.create().show();
    }


    @Override
    public void onItemClick(SettingsItem item, int pos) {

        switch (item.getType()) {
            case SET_ITEM_EMAIL:
            case SET_ITEM_PHONE:
                break;

            case SET_ITEM_PASSWORD: // 修改密码
                ContainerActivity.go(activity, ChangePasswordFragment.class, null);
                break;
            case SET_ITEM_PROTECT:
                ContainerActivity.go(activity, SetProtectionFragment.class, null);
                break;
            case SET_ITEM_LOGOUT: // 注销登录
                onClickLogout();
                break;

            case SET_ITEM_ABOUT: // 关于
                ContainerActivity.go(activity, AboutFragment.class, null);
                break;

            case SET_ITEM_CHECKUPDATE: // 检查更新
                checkUpdate();
                break;

            case SET_ITEM_FEEDBACK: // 用户反馈
                feedback();
                break;

            case SET_ITEM_HELP: // 帮助
                ContainerActivity.go(activity, HelpFragment.class, null);
                break;
            // case SET_ITEM_VIEW_DESC:// = 14;
            //     Intent intent = new Intent(activity, WalkThoughActivity.class);
            //     intent.putExtra("fname", WalkthoughFragment.class.getName());
            //     activity.startActivity(intent);
            //     break;
            case SET_ITEM_LOCK: // 图案解锁
                Bundle arg = new Bundle();
                // arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_SET);
                arg.putInt(SetLockFragment.EXTRA_TYPE, SetLockFragment.EXTRA_TYPE_SET);
                ContainerActivity.go(activity, SetLockFragment.class, arg);
                // ContainerActivity.go(SplashActivity.this, SetLockFragment.class, arg);
                break;
            // case SET_ITEM_HTTP: // http解锁
            // ContainerActivity.go(activity, HttpFragment.class, null);
            //  break;
            case SET_LANGUAGE: // 设置语言
                showSetLanguageDialog();
                break;
        }


    }

    private class ViewHolder {
        View rootView;
        XRecyclerView recyclerView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            // titleBar =rootView.findViewById(R.id.ti)
            recyclerView = rootView.findViewById(R.id.recycler_view);
            recyclerView.setPullRefreshEnabled(false);
            recyclerView.setLoadingMoreEnabled(false);
        }


    }

}
