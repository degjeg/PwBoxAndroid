package com.pw.box.tool;

import android.util.Log;
import android.widget.Toast;

import com.pw.box.App;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Http 服务器的工具类
 * Created by danger on 2016/11/28.
 */

public class HttpServer {

    private static final String TAG = "HttpServer";
    private static final HttpServer httpServer = new HttpServer();
    ServerSocket serverSocket;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    private HttpServer() {
    }

    public static HttpServer get() {
        return httpServer;
    }

    public void show(String s) {
        Toast.makeText(App.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void start() {
        singleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(8080);
                    // serverSocket.bind(new InetSocketAddress("localhost", 80));
                    byte[] buffer = new byte[1024];
                    while (true) {
                        Socket s = serverSocket.accept();
                        InputStream is = s.getInputStream();
                        int readBytes = 0;
                        Log.e(TAG, "新连接，开始读取数据");
                        do {
                            readBytes = is.read(buffer);
                            Log.e(TAG, "读取到：" + readBytes + " 字节");
                            if (readBytes > 0) {
                                String content = new String(buffer, 0, readBytes);

                                Log.e(TAG, "recv:" + content);
                                if (content.contains("\r\n\r\n")) {
                                    break;
                                }
                            }
                        } while (readBytes > 0);

                        OutputStream os = s.getOutputStream();
                        os.write("您好，您收到了来自服务器的新年祝福！\n".getBytes("utf-8"));
                        os.close();
                        s.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        singleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    serverSocket = null;
                }
            }
        });
    }
}