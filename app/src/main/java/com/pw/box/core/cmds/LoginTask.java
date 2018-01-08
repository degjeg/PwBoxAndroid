package com.pw.box.core.cmds;

import android.content.Intent;
import android.widget.Toast;

import com.pw.box.App;
import com.pw.box.R;
import com.pw.box.bean.protobuf.LoginReq;
import com.pw.box.bean.protobuf.LoginRes;
import com.pw.box.cache.Cache;
import com.pw.box.cache.User;
import com.pw.box.core.Cm;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.group.GetGroupListTask;
import com.pw.box.tool.StatTool;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.accounts.LoginFragment;
import com.pw.box.utils.Aes256;
import com.pw.box.utils.L;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 * 用户登录任务
 * Created by danger on 16/9/12.
 */
public class LoginTask extends Task<LoginRes> {

    String account;
    byte[] passwordFilled;
    byte[] passwordFilled1;

    LoginHandler listener;

    /* public LoginTask(String account, String password, LoginHandler loginHandler) {
        this.account = account;
        this.password = password;
        this.listener = loginHandler;
    } */
    N.NetHandler<LoginRes> cb = new N.NetHandler<LoginRes>() {
        @Override
        public void onSuccess(LoginRes retPack) {
            synchronized (isCanceled) {
                if (isCanceled.get()) {
                    return;
                }
                if (retPack.retCode == ErrorCodes.SUCCESS) {
                    StatTool.trackEvent(StatTool.EVENT_LOGIN, retPack.retCode);

                    // save data to local ☟
                    // Cache.get().setLoginStatus(Cache.LOGIN_STATUS_LOGGIN);
                    // M.LoginReq loginReq = (M.LoginReq) reqPack;
                    User user = Cache.get().getUser();
                    // Cache.get().setLoginStatus(Cache.LOGIN_STATUS_LOGGIN);
                    user.setHavePretection(retPack.have_protection);
                    user.setAccount(account);
                    user.setPwFilledLogin(passwordFilled/*loginReq.getPassword().toByteArray()*/);
                    user.setPwFilledRawKey(passwordFilled1);
                    byte[] rawKeyEncByPw = retPack.raw_key_by_pw.toByteArray();

                    try {
                        user.setRawKey(Aes256.decrypt(rawKeyEncByPw, passwordFilled1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    // save data to local ☝︎
                    Cache.get().onLoginSuccess();
                    if (listener != null) {
                        listener.onLoginSuccess(false);
                    }

                    new GetGroupListTask().execute();

                    if (Cache.get().getDataVersion() != retPack.data_version
                            || Cache.get().getMaxDataId() != retPack.max_data_id) {
                        Cache.get().clearAllData();
                        Cache.get().setDataVersion(0);

                        // Cm.get().syncData(); // 同步数据
                        Cache.get().syncData();
                        // GroupManager.get().
                    } else {
                        Cache.get().onSyncDataSuccess(); // 通知数据同步成功
                    }
                } else {
                    onFail(retPack.retCode, null);
                }
            }
        }

        @Override
        public void onFail(int localErrorCode, Exception e) {
            StatTool.trackEvent(StatTool.EVENT_LOGIN, localErrorCode);
            synchronized (isCanceled) {
                // Cache.get().setLoginStatus(Cache.LOGIN_STATUS_INIT);
                if (localErrorCode > 0) { // 服务器告知登录失败
                    // Cm.get().notifyStatusChange(ConnectionListener.STATUS_LOGIN_FAIL);
                    Cache.get().onLoginFail();
                    if (listener == null || !(listener instanceof LoginFragment)) {
                        Intent intent = new Intent(App.getContext(), ContainerActivity.class);

                        intent.putExtra("fname", LoginFragment.class.getName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(App.getContext(), R.string.error_please_relogin, Toast.LENGTH_SHORT).show();
                        App.getContext().startActivity(intent);
                    }
                }
                if (!isCanceled.get()) {
                    if (listener != null) {
                        listener.onLoginFail(localErrorCode);
                    }
                }
            }
        }
    };

    public LoginTask(String account, byte[] passwordFilled, LoginHandler loginHandler) {
        // needLogin = false;
        // this.account = account;
        // this.passwordFilled = passwordFilled;
        // this.listener = loginHandler;
        this(account, passwordFilled, null, loginHandler);
    }

    public LoginTask(String account, byte[] passwordFilled, byte[] passwordFilled1, LoginHandler listener) {
        if (L.E) L.get().e(TAG, "", null);
        needLogin = false;
        cmd = CmdIds.LOGIN;
        this.account = account;
        this.passwordFilled = passwordFilled;
        this.passwordFilled1 = passwordFilled1;
        this.listener = listener;
        setCallBack(cb);

        if (listener != null) {
            this.listener = listener;
        } else {
            this.listener = new LoginHandler() {
                @Override
                public void onLoginSuccess(boolean isIgnored) {
                    // if(!isIgnored)
                    // Cm.get().notifyStatusChange(ConnectionListener.STATUS_LOGGEDIN);
                }

                @Override
                public void onLoginFail(int code) {
                    // Cm.get().notifyStatusChange(ConnectionListener.STATUS_LOGIN_FAIL);
                }
            };
        }
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    protected Message prepareData() throws Exception {
        // Cache.get().setLoginStatus(Cache.LOGIN_STATUS_LOGGING);
        LoginReq.Builder builder = new LoginReq.Builder();
        builder.account(account);

        // notifyStatusChange(ConnectionListener.STATUS_LOGGING);
        // builder.setPassword(ByteString.copyFrom(Aes256.encrypt(account.getBytes(), password, Aes256.FILL_TYPE_PW)));
        builder.password(ByteString.of(Aes256.encrypt(account.getBytes(), passwordFilled)));
        builder.device_info(Cm.get().deviceInfo);
        return builder.build();
    }

    @Override
    public void run() {
        try {
            // Cache.get().onLoginStart();
            super.run();
        } finally {
            if (L.E) logger.d(TAG, "login task finish");
        }
    }

    public interface LoginHandler {
        void onLoginSuccess(boolean isIgnored);

        void onLoginFail(int code);
    }
}
