package com.pw.box.core.cmds;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.pw.box.cache.Cache;
import com.pw.box.cache.SimpleUserStatusChangeListener;
import com.pw.box.cache.User;
import com.pw.box.core.Cm;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.bean.Pack;
import com.pw.box.net.ConnectionListener;
import com.pw.box.utils.L;
import com.squareup.wire.ProtoAdapter;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 网络请求的工具类
 * Created by danger on 16/10/26.
 */

public abstract class Task<T> implements
        Runnable,
        ConnectionListener {


    protected static final String TAG = "Task_";
    protected final AtomicBoolean isCanceled = new AtomicBoolean(false);
    protected final AtomicBoolean isFinished = new AtomicBoolean(false);
    protected boolean needLogin = true;
    protected int timeout = 10000;
    protected N.NetHandler<T> callBack;
    protected int cmd;
    protected Object req;
    protected L logger = L.get();
    protected Handler handler;
    protected Pack pack;
    SimpleUserStatusChangeListener userStatusChangeListener = new SimpleUserStatusChangeListener() {
        @Override
        public void onUserLoginSuccess() {
            super.onUserLoginSuccess();
            if (needLogin) { // 如果该任务需要登录,在登录成功时立刻发送请求
                sendRequest();
            }
        }
    };

    public Task() {
        this(true);
    }

    public Task(boolean needLogin) {
        this.needLogin = needLogin;


    }

    public Task setCallBack(N.NetHandler<T> callBack) {
        this.callBack = callBack;
        return this;
    }

    protected abstract Object prepareData() throws Exception;

    protected void notifyFail(final int err, final Exception e) {
        if (isFinished.get()) return;
        clean();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (L.E)
                    logger.e(TAG, String.format(Locale.getDefault(), "[%d]notifyFail %d", cmd, err), e);
                if (L.E) logger.e(TAG, "" + isCanceled + "," + isFinished + "," + callBack);
                if (callBack != null && !isCanceled.get()) {
                    callBack.onFail(err, e);
                }
            }
        });
    }

    protected void notifySuccess(final T retPack) {
        if (isFinished.get()) return;
        clean();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null && !isCanceled.get()) {
                    callBack.onSuccess(retPack);
                } else {
                    if (L.E) logger.e(TAG, "" + isCanceled + "," + callBack);
                }

            }
        });
    }

    public void cancel() {
        isCanceled.set(true);
        if (L.E)
            logger.e(TAG, String.format(Locale.getDefault(), "[%d][%d]cancel", cmd, pack == null ? 0 : pack.getReqcode()));
        clean();
    }

    private void clean() {
        isFinished.set(true);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        Cm.get().removeConnectionListener(this);
        Cache.get().removeUserStatusChangeListener(userStatusChangeListener);
    }

    protected String getAccount() {
        if (Cm.get().getConnection() != null) { // 有连接，使用当前连接的账号
            return Cm.get().getAccount();
        }
        User user = Cache.get().getUser(); // 无连接，使用登录账号的
        if (user != null) {
            return user.getAccount();
        }
        return "";
    }

    @Override
    public void run() {
        try {

            if (Cm.get().getConnection() != null) {
                if (!TextUtils.equals(Cm.get().getConnection().getAccount(), getAccount())) {
                    Cm.get().close();
                    Thread.sleep(300);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            if (L.E) logger.e(TAG, " fail", e);
            notifyFail(ErrorCodes.CONNECT_FAIL, e);
            // onFail(ErrorCodes.PREPAREDATA, req, null);
            return;
        }

        Cm.get().addConnectionListener(this);
        Cache.get().addUserStatusChangeListener(userStatusChangeListener);
        Cm.get().connect(getAccount());

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyFail(ErrorCodes.TIMEOUT, null);
            }
        }, timeout);
    }

    public void execute() {
        new Thread(this).start();
    }

    @Override
    public void onIdle() {

    }

    @Override
    public void onConnected() {
        // if(L.E) logger.e(TAG, String.format(Locale.ENGLISH, "[%d]req:", (int) cmd) + req);
        // Cm.get().sendPack((short) cmd, req, this);
        if (!needLogin) { // 如果该任务不需要登录,在连接成功时立刻发送请求
            sendRequest();
        }
    }

    @Override
    public void onClosed(Exception e) {
        notifyFail(ErrorCodes.NO_NET, e);
    }

    @Override
    public void onReceive(Pack pack) {
        if (this.pack == null || pack.getReqcode() != this.pack.getReqcode()) {
            return;
        }
        Object message = null;
        if (pack.getData() != null) {
            ProtoAdapter parser = N.parses.get(pack.getCommand());
            if (parser != null) {
                try {
                    message = parser.decode(pack.getData());

                    if (L.D) L.get().d(Cm.TAG, "receive:[" + pack.getCommand() + "]" + message);
                } catch (Exception e) {
                    if (L.E) L.get().e(Cm.TAG, "parse receive:", e);
                }
            }
        }
        // task.handler.onSuccess(task.reqPack, message);
        // handler.post(new N.Notifier(task, message));
        notifySuccess((T) (message != null ? message : pack.getData()));
    }

    private void sendRequest() {
        try {
            req = prepareData();
        } catch (Exception e) {
            if (L.E) logger.e(TAG, "prepareData fail", e);
            notifyFail(ErrorCodes.PREPAREDATA, e);
            return;
        }
        pack = Cm.get().sendPack((short) cmd, req);
        if (pack == null) {
            notifyFail(ErrorCodes.UNKNOWN_ERROR, null);
            // callBack.onFail(ErrorCodes.UNKNOWN_ERROR, null, null);
        }
    }
}
