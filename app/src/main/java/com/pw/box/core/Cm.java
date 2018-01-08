package com.pw.box.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.pw.box.App;
import com.pw.box.bean.protobuf.DeviceInfo;
import com.pw.box.core.bean.Pack;
import com.pw.box.net.MySslConnection;
import com.pw.box.utils.L;
import com.squareup.wire.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ConnectionManager
 * Created by danger on 16/9/3.
 */
public class Cm implements com.pw.box.net.ConnectionListener {

    public static final java.lang.String TAG = "ConnectionManager";
    private static final Cm cm = new Cm();
    public final DeviceInfo deviceInfo = getDeviceInfo();
    final ReentrantLock lock = new ReentrantLock();
    final ReentrantLock closeLock = new ReentrantLock();
    final List<com.pw.box.net.ConnectionListener> listeners = new ArrayList<>();
    public Random random = new Random(System.currentTimeMillis());
    L logger = L.get();
    int port;
    CountDownLatch closeLatch;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private MySslConnection connection;

    ///public LoginTask loginTask;
    ///DataSyncTask dataSyncTask;
    ///public boolean syncDataSuccess = false;

    ///HashMap<String, ConnectionListener> connectionListeners = new HashMap<>();
    private String host;
    private Object question;

    public static Cm get() {
        return cm;
    }

    public void init(String host, int port) {
        this.host = host;
        this.port = port;

        if (L.E) logger.d(TAG, "init:" + host + ":" + port);
    }

    public String getAccount() {
        if (connection != null) {
            return connection.getAccount();
        }
        return "";
    }

    public synchronized void connect(String account) {
        try {
            lock.lock();

            if (connection != null && TextUtils.equals(connection.getAccount(), account)) {
                return;
            }

            if (connection != null) {
                close();
            }

            if (L.E) logger.e(TAG, "connecting" + account + connection, null);
            // notifyStatusChange(ConnectionListener.STATUS_CONNECTING);

            connection = new MySslConnection(account);
            if (L.E) logger.e(TAG, "connected" + account + connection);
        } finally {
            lock.unlock();
        }

        connection.addConnectionListener(this);
        for (com.pw.box.net.ConnectionListener listener : listeners) {
            connection.addConnectionListener(listener);
        }
        connection.init(host, port);

        //        connection = new PwBoxConnection() {
        //
        //            @Override
        //            public void onClosed(Exception e) {
        //                if(L.E) logger.e(TAG, "closed");
        //                Cm.this.close();
        //            }
        //
        //            @Override
        //            public void onConnected() {
        //                if(L.E) logger.e(TAG, "connected");
        //                status.set(STATUS_CONNECTED);
        //
        //                notifyStatusChange(ConnectionListener.STATUS_CONNECTED);
        //                // new GetKeyTask().execute();
        //                try {
        //                    getKey();
        //                } catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //
        //            }
        //        };
        //        connection.init(App.getContext(), host, port);
        connection.connect();
    }

    public void close() {

        closeLock.lock();
        try {
            closeLatch = new CountDownLatch(1);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // listeners.clear();
                        if (connection == null) {
                            return;
                        }
                        if (L.D) logger.d(TAG, "close" + connection.getAccount());
                        connection.close();
                        connection = null;
                        if (L.D) logger.d(TAG, "close finish");

                    } finally {
                        closeLatch.countDown();
                    }
                    // Cache.get().setLoginStatus(Cache.LOGIN_STATUS_INIT);
                    // notifyStatusChange(ConnectionListener.STATUS_CLOSED);
                }
            });
            t.start();
            try {
                closeLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                //   e.printStackTrace();
            }
        } finally {
            closeLock.unlock();
        }

    }


    //    public int checkConnected(N.NetHandler handler) {
    //        // 未连接网络,直接报失败,无需重试
    //        if (!NetWorkUtils.isConnected(App.getContext())) {
    //            if (handler != null) handler.onFail(ErrorCodes.NO_NET, null, null);
    //            return -1;
    //        }
    //
    //        // 有网络,但未获取到服务器地址
    //        if (TextUtils.isEmpty(host)) {
    //            // 还没有获取到服务器的地址和端口
    //            if (!GetServerAddrTask.haveTask) {
    //                new GetServerAddrTask().enqueue();
    //            }
    //            if (handler != null) handler.onFail(ErrorCodes.NO_NET, null, null);
    //            return 1;
    //        }
    //
    //        if (status.get() == STATUS_INIT) {
    //            connect();
    //        }
    //        if (status.get() != STATUS_CONNECTED
    //                ) {
    //            if (handler != null) handler.onFail(ErrorCodes.TRY_AGAIN, null, null);
    //            return 2;
    //        }
    //
    //        if (connection.getSignKey() == null) {
    //            try {
    //                // getKey();
    //                if (handler != null) handler.onFail(ErrorCodes.TRY_AGAIN, null, null);
    //                return 4;
    //            } catch (Exception e) {
    //                // e.printStackTrace();
    //                return -2;
    //            }
    //        }
    //
    //        return 0;
    //    }
    //
    //    public int checkLogin(N.NetHandler handler) {
    //        int connectStatus = checkConnected(handler);
    //        if (0 != connectStatus) {
    //            return connectStatus;
    //        }
    //
    //        if (!Cache.get().isLogin()) {
    //            if (handler != null) handler.onFail(ErrorCodes.NOT_LOGIN, null, null);
    //            return 10;
    //        }
    //        return 0;
    //    }
    //
    //    public void sendPack(short cmd, final AbstractMessage message, final N.NetHandler handler) {
    //        try {
    //            synchronized (this) {
    //                if (connection != null) {
    //                    connection.sendPack(cmd, message, handler);
    //                    if(L.D) L.get().d(TAG, "sendPack:[" + cmd + "]," + message);
    //                } else {
    //                    new Handler(Looper.getMainLooper()).post(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            handler.onFail(ErrorCodes.NOT_CONNECTED, message, null);
    //                        }
    //                    });
    //                    // handler.onFail(ErrorCodes.NOT_CONNECTED, message, null);
    //                }
    //            }
    //
    //        } catch (final Exception e) {
    //            if(L.E) L.get().e(TAG, "", e);
    //
    //            new Handler(Looper.getMainLooper()).post(new Runnable() {
    //                @Override
    //                public void run() {
    //                    handler.onFail(ErrorCodes.UNKNOWN_ERROR, null, e);
    //                }
    //            });
    //        }
    //    }


    //    private void login(String account, byte[] password1, byte[] password2, LoginTask.LoginHandler handler) {
    //        // if (Cache.get().getLoginStatus() == Cache.LOGIN_STATUS_LOGGIN) {
    //        //     if(L.D) logger.d(TAG, "login ignored");
    //        //     if (handler != null) handler.onLoginSuccess(true);
    //        //     if (!syncDataSuccess && dataSyncTask == null) {
    //        //         syncData();
    //        //     }
    //        //     return;
    //        // }
    //        // if (loginTask != null) {
    //        //     // loginTask.cancel();
    //        //     if(L.D) logger.d(TAG, "is logging");
    //        //     return;
    //        // }
    //        //
    //        // if (dataSyncTask != null) {
    //        //     if(L.D) logger.d(TAG, "cancel sync data");
    //        //     dataSyncTask.cancel();
    //        // }
    //
    //        LoginTask loginTask = new LoginTask(account, password1, password2, handler);
    //        // Cache.get().setLoginStatus(Cache.LOGIN_STATUS_LOGGING);
    //        // notifyStatusChange(ConnectionListener.STATUS_LOGGING);
    //        loginTask.execute();
    //    }

    //    public void getDataList(int localVer, long fromId, N.NetHandler<M.GetItemListRes> handler) {
    //        try {
    //            M.GetItemListReq.Builder builder = M.GetItemListReq.newBuilder();
    //            builder.setV(localVer);
    //            builder.setFrom(fromId);
    //            sendPack(CmdIds.GET_ITEM_LIST, builder.build(), handler);
    //        } catch (Exception e) {
    //            // e.printStackTrace();
    //            handler.onFail(ErrorCodes.UNKNOWN_ERROR, null, e);
    //        }
    //    }


    DeviceInfo getDeviceInfo() {
        DeviceInfo.Builder builder = new DeviceInfo.Builder();
        String android_id = Settings.Secure.getString(App.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        builder.device_id(android_id);

        String productBrand = Build.BRAND;
        String productModel = Build.MODEL;


        builder.device_name(productBrand + "," + productModel);
        return builder.build();
    }

    //
    //    private void autoLogin() {
    //        User user = Cache.get().getUser();
    //
    //        // 不满足自动登录的条件
    //        if (TextUtils.isEmpty(user.getAccount()) || null == user.getPwFilledLogin()) {
    //            return;
    //        }
    //
    //        login(user.getAccount(),
    //                user.getPwFilledLogin(),
    //                user.getPwFilledRawKey(),
    //                new LoginTask.LoginHandler() {
    //
    //                    @Override
    //                    public void onLoginSuccess(boolean isIgnored) {
    //                        // if (!isIgnored) notifyStatusChange(ConnectionListener.STATUS_LOGGEDIN);
    //                    }
    //
    //                    @Override
    //                    public void onLoginFail(int code) {
    //                        // notifyStatusChange(ConnectionListener.STATUS_LOGIN_FAIL);
    //                    }
    //                });
    //    }

    //    public void syncData() {
    //        // 防止重复启动同步任务
    //        if (dataSyncTask != null) {
    //            dataSyncTask.cancel();
    //        }
    //
    //        Cm.get().notifyStatusChange(ConnectionListener.STATUS_SYNCDATA_START);
    //        dataSyncTask = new DataSyncTask();
    //        dataSyncTask.syncDataList();
    //    }
    //
    //    public void syncDataFail() {
    //        syncDataSuccess = false;
    //        notifyStatusChange(ConnectionListener.STATUS_SYNCDATA_FAIL);
    //        dataSyncTask = null;
    //    }
    //
    //    public void syncDataSuccess() {
    //        syncDataSuccess = true;
    //        notifyStatusChange(ConnectionListener.STATUS_SYNCDATA_SUCCESS);
    //        dataSyncTask = null;
    //    }
    //
    public void addConnectionListener(com.pw.box.net.ConnectionListener listener) {
        // addConnectionListener(listener.getClass().getName(), listener);
        synchronized (listeners) {
            listeners.remove(listener);
            listeners.add(listener);
            if (connection != null) {
                connection.addConnectionListener(listener);
            }
        }
    }
    //
    //    public void addConnectionListener(String tag, ConnectionListener listener) {
    //        synchronized (connectionListeners) {
    //            connectionListeners.put(tag, listener);
    //            listener.onConnectionStatusChange(dataStatus.get());
    //        }
    //    }
    //
    //
    //    public void removeConnectionListener(ConnectionListener listener) {
    //        removeConnectionListener(listener.getClass().getName());
    //    }
    //
    //    public void removeConnectionListener(String tag) {
    //        synchronized (connectionListeners) {
    //            connectionListeners.remove(tag);
    //        }
    //    }
    //
    //    public void notifyStatusChange(final int status) {
    //        if(L.E) logger.e(TAG, "sts:" + this.dataStatus + "->" + status);
    //        this.dataStatus.set(status);
    //
    //        Handler handler = new Handler(Looper.getMainLooper());
    //        handler.post(new Runnable() {
    //            @Override
    //            public void run() {
    //                synchronized (connectionListeners) {
    //                    for (ConnectionListener listener : connectionListeners.values()) {
    //                        listener.onConnectionStatusChange(status);
    //                    }
    //                }
    //            }
    //        });
    //    }


    public String getHost() {
        return host;
    }

    public MySslConnection getConnection() {
        return connection;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void onIdle() {

    }

    @Override
    public void onConnected() {
        // autoLogin();
    }

    @Override
    public void onClosed(Exception e) {
        // close();
        connection = null;
    }

    @Override
    public void onReceive(Pack buffer) {

    }

    public void removeConnectionListener(com.pw.box.net.ConnectionListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            if (connection != null) {
                connection.removeConnectionListener(listener);
            }
        }
    }

    public Pack sendPack(short cmd, Object req) {
        if (connection != null) {
            synchronized (listeners) {
                if (req == null)
                    return connection.sendPack(cmd, (byte[]) null);
                    // if(req instanceof AbstractMessage)
                    //     return connection.sendPack(cmd, (AbstractMessage)req);
                else if (req instanceof Message)
                    return connection.sendPack(cmd, (Message) req);
            }
        }
        return null;
    }
}
