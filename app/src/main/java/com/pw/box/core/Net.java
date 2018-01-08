package com.pw.box.core;

import android.os.Handler;
import android.os.Looper;

import com.common.bean.BaseRes;
import com.pw.box.App;
import com.pw.box.bean.protobuf.CheckAppVerResponse;
import com.pw.box.bean.protobuf.GetHostRes;
import com.pw.box.cache.Constants;
import com.pw.box.core.bean.Pack;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.net.MyByteBuffer;
import com.pw.box.net.PackDecoder;
import com.pw.box.utils.L;
import com.pw.box.utils.NetWorkUtils;
import com.squareup.wire.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;

/**
 * 网络解包打包工具类
 * Created by danger on 16/10/19.
 */

public class Net implements Runnable {


    public static final int NOTIFY_TO_UITHREAD = 0;
    public static final int NOTIFY_TO_UITOTHISTHREAD = 1;
    AtomicBoolean isCanceled = new AtomicBoolean(false);
    boolean isExecuted = false;
    Socket socket;
    NetHandler callback;
    Message req;
    int cmd;
    L logger = L.get();
    List<Object> packList = new ArrayList<>();
    private String TAG = "net_";
    PackDecoder packDecoder = new PackDecoder(TAG) {
        @Override
        public void getAPack(Pack pack) {
            super.getAPack(pack);
            packList.add(pack);
        }
    };
    // 结果通知给ui线程还是当前线程
    private int notifyToThread = NOTIFY_TO_UITHREAD;
    private String host = Constants.PROXY_HOST;
    private int port = Constants.PROXY_PORT;

    public Net(int cmd, Message req, NetHandler callback) {
        this(cmd, req, callback, NOTIFY_TO_UITHREAD);
    }

    public Net(int cmd, Message req, NetHandler callback, int notifyToThread) {
        this.cmd = cmd;
        this.req = req;
        this.callback = callback;
        this.notifyToThread = notifyToThread;
        TAG = String.format(Locale.getDefault(), "net_[%d]", cmd);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAddr(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void execute() {
        ThreadPool.execute(this);
    }

    @Override
    public void run() {
        if (isExecuted) {
            return;
        }
        isExecuted = true;


        try {
            Thread.sleep(3000);


            socket = new Socket();
            // 网络未连接
            if (!NetWorkUtils.isConnected(App.getContext())) {
                if (L.E) logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]no net:", cmd));
                notifyFail(-2, null);
                return;
            }

            if (L.E) logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]req:", cmd) + req);
            InetSocketAddress isa = new InetSocketAddress(host, port);

            socket.setSoTimeout(Constants.NET_TIMEOUT);
            socket.setKeepAlive(false);
            socket.connect(isa, Constants.NET_TIMEOUT);
            if (L.E) logger.d(TAG, "connected to " + host + ":" + port);

            if (isCanceled()) {
                return;
            }
            // ByteBuf byteBuf = Unpooled.buffer();
            MyByteBuffer byteBuf = new MyByteBuffer(4000);

            Pack pack = PacketCreator.createPack((short) cmd, (short) 1, req, null);
            pack.write(byteBuf);

            socket.getOutputStream().write(byteBuf.get(byteBuf.readableBytes()));
            byteBuf.clear();

            // ClientPackDecoder decoder = new ClientPackDecoder();
            byte[] tmpBuf = new byte[1024];
            int readed;

            do {
                readed = socket.getInputStream().read(tmpBuf);

                if (L.E) logger.e(TAG, "receive:" + readed + " bytes");
                if (readed <= 0) {
                    break;
                }
                byteBuf.writeBytes(tmpBuf, 0, readed);
                packDecoder.onReceive(byteBuf);
                // decoder.decode(null, byteBuf, packList);
                if (!packList.isEmpty()) {
                    break;
                }
            } while (readed > 0);

            if (packList.isEmpty()) {
                // decoder.decode(null, byteBuf, packList);
                packDecoder.onReceive(byteBuf);
            }
            notifyResult(packList);

            // readed = socket.getInputStream().read(tmpBuf);
            // L.get().d("read:"+readed);
        } catch (java.net.SocketTimeoutException e) {
            if (L.E) logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]error", cmd), e);
            notifyFail(ErrorCodes.TIMEOUT, e);
        } catch (IOException e) {
            if (L.E) logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]error", cmd), e);
            notifyFail(ErrorCodes.NOT_CONNECTED, e);
        } catch (Exception e) {
            if (L.E) logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]error", cmd), e);
            notifyFail(ErrorCodes.UNKNOWN_ERROR, e);
        } finally {
            close();
        }
    }


    private Message parseData(byte[] data) throws IOException {
        if (cmd == CmdIds.GET_HOST) {
            return GetHostRes.ADAPTER.decode(data);
        } else if (cmd == CmdIds.CHECK_VER) {
            return CheckAppVerResponse.ADAPTER.decode(data);
        } else if (cmd == CmdIds.FEEDBACK) {
            return BaseRes.ADAPTER.decode(data);
        }
        return null;
    }

    private void notifyResult(final List<Object> packList) {
        if (packList.isEmpty()) {
            notifyFail(-1, null);
        } else {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (isCanceled() || callback == null) {
                        return;
                    }
                    // callback.c
                    Pack pack = (Pack) packList.get(0);
                    // callback.getClass().
                    Message msg = null;
                    try {
                        msg = parseData(pack.getData());
                        if (L.E)
                            logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]ret:%s", cmd, msg == null ? "" : msg));
                    } catch (Exception e) {
                        if (L.E)
                            logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]parse error", cmd), e);
                    }

                    try {
                        callback.onSuccess(cmd, req, msg);
                    } catch (Exception e) {
                        if (L.E)
                            logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]notify success", cmd), e);
                        try {
                            callback.onFail(cmd, req, -3, e);
                        } catch (Exception e1) {
                            // e1.printStackTrace();
                            if (L.E)
                                logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]success fail ", cmd), e);
                        }
                    }
                }
            };

            if (notifyToThread == NOTIFY_TO_UITHREAD) {
                new Handler(Looper.getMainLooper()).post(r);
            } else {
                r.run();
            }
        }
    }


    private void notifyFail(final int code, final Throwable t) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (!isCanceled() && callback != null) {
                    try {
                        callback.onFail(cmd, req, code, t);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        if (L.E)
                            logger.e(TAG, String.format(Locale.getDefault(), "cmd[%d]when notify fail", cmd), e);
                    }
                }
            }
        };

        if (notifyToThread == NOTIFY_TO_UITHREAD) {
            new Handler(Looper.getMainLooper()).post(r);
        } else {
            r.run();
        }
    }

    public void cancel() {
        synchronized (isCanceled) {
            isCanceled.set(true);
            close();
        }
    }

    public boolean isCanceled() {
        return isCanceled.get();
    }


    private void close() {
        if (socket != null) {
            synchronized (this) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // close silently
                    // e.printStackTrace();
                }
            }
        }
    }

    public interface NetHandler<T extends Message> {
        void onSuccess(int cmd, Message req, T response);

        void onFail(int cmd, Message req, int code, Throwable e);
    }
}
