package com.pw.box.net;


import android.os.SystemClock;

import com.pw.box.utils.L;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 长连接工具类
 * Created by danger on 2016/12/2.
 */
abstract public class NioConnection {

    public static long S_ID = 0;
    protected final long id;
    final List<byte[]> pendingData = new ArrayList<>();
    final MyByteBuffer recvBuf = new MyByteBuffer(66 * 1024);
    public String TAG = "NioConnection";
    public String host; //
    public int port; //

    Thread sendThread;
    Thread receiveThread;
    Thread timingThread;

    Socket socket;
    InputStream is;
    OutputStream os;
    boolean isConnected = false;
    boolean isDestoryed = false;
    // Context context;
    ConnectionListener listener;
    PackDecoder packDecoder;
    private int idleTime = 15000; // 连接空闲间隔
    private int TIMEOUT = 20000; // 读取超时时间
    private AtomicLong lastReceiveDataTime = new AtomicLong(SystemClock.elapsedRealtime());
    private Runnable receiveRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                doConnect();

                // onConnected();
                listener.onConnected();

                doReceive();
            } catch (IOException e) {
                // e.printStackTrace();
                if (L.E) L.get().e(TAG, "receive", e);
                close();
            } catch (InterruptedException e) {
                // e.printStackTrace();
                if (L.E) L.get().e(TAG, "receive", e);
            }
        }
    };
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                doSend();
            } catch (InterruptedException e) {
                // e.printStackTrace();
                if (L.E) L.get().e(TAG, "send", e);
            } catch (IOException e) {
                // e.printStackTrace();
                if (L.E) L.get().e(TAG, "send", e);

                close();
            }
        }
    };
    private Runnable timingRunnable = new Runnable() {
        @Override
        public void run() {
            while (!isDestoryed) {
                try {
                    Thread.sleep(3000);
                    if (SystemClock.elapsedRealtime() - lastReceiveDataTime.get() >= idleTime) {
                        lastReceiveDataTime.set(SystemClock.elapsedRealtime());
                        listener.onIdle();
                    }
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    break;
                }
            }
        }
    };


    public NioConnection() {
        id = ++S_ID;
        TAG = String.format(Locale.getDefault(), "NioConnection[%d]", id);
    }

    public void init(ConnectionListener listener, String host, int port) {
        this.listener = listener;

        this.host = host;
        this.port = port;
    }


    //    public Context getContext() {
    //        return context;
    //    }

    public void connect() {
        if (receiveThread != null) {
            return;
        }

        receiveThread = new Thread(receiveRunnable, "receive" + id);
        receiveThread.start();

        sendThread = new Thread(sendRunnable, "send" + id);
        sendThread.start();

        timingThread = new Thread(timingRunnable, "timing" + id);
        timingThread.start();
    }

    public void close() {
        if (L.E) L.get().e(TAG, "close");
        if (isDestoryed) {
            return;
        }
        isDestoryed = true;

        if (listener != null) listener.onClosed(null);

        if (sendThread != null) sendThread.interrupt();
        if (receiveThread != null) receiveThread.interrupt();
        if (timingThread != null) timingThread.interrupt();

        sendThread = null;
        receiveThread = null;
        timingThread = null;

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // close silently
            } finally {
                socket = null;
            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // close silently
            } finally {
                is = null;
            }
        }

        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                // close silently
            } finally {
                os = null;
            }
        }
    }

    public void sendData(byte[] pack) {
        synchronized (pendingData) {
            pendingData.add(pack);
            pendingData.notifyAll();
        }
        if (L.E) L.get().d(TAG, "send " + pack.length + "bytes:" + Arrays.toString(pack));
        // L.get().e(TAG, "send ", new Throwable());
    }

    void doSend() throws InterruptedException, IOException {
        while (!isDestoryed) {
            byte[] data;
            synchronized (pendingData) {
                while (pendingData.isEmpty()) {
                    if (L.E) L.get().e(TAG, "send wait for pack");
                    pendingData.wait();
                    if (L.E) L.get().e(TAG, "send wait for pack ok");
                }
                data = pendingData.get(0);
            }

            os.write(data);
            if (L.E) L.get().e(TAG, "a pack is sent " + data.length);

            synchronized (pendingData) {
                pendingData.remove(data);
            }
        }
    }

    void doConnect() throws IOException {
        if (L.D) L.get().d(TAG, "connect:" + host + ":" + port);
        socket = new Socket(host, port);
        socket.setSoTimeout(TIMEOUT);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        isConnected = true;
    }

    void doReceive() throws InterruptedException, IOException {
        byte[] bufTmp = new byte[1024];

        while (!isDestoryed) {
            int receivedCount = is.read(bufTmp);

            if (L.D) L.get().d(TAG, "recv " + receivedCount + " bytes");
            if (receivedCount > 0) {
                lastReceiveDataTime.set(SystemClock.elapsedRealtime());
                recvBuf.writeBytes(bufTmp, 0, receivedCount);
                try {
                    // onReceive(recvBuf);
                    packDecoder.onReceive(recvBuf);
                } catch (Exception e) {
                    // e.printStackTrace();
                    if (L.E) L.get().e(TAG, "doReceive", e);
                }
            } else if (receivedCount < 0) {
                throw new IOException("connection is closed!");
            }
        }
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }
}
