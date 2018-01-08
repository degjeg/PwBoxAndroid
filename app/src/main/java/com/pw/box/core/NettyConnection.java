//package com.pw.box.core;
//
//
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.os.SystemClock;
//
//
//import com.pw.box.cache.Constants;
//import com.pw.box.core.bean.Pack;
//import com.pw.box.core.cmds.CmdIds;
//import com.pw.box.utils.Aes256;
//import com.pw.box.utils.L;
//
//import java.util.Random;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//
///**
// * Created by danger on 16/8/19.
// */
//public abstract class NettyConnection implements Handler.Callback {
//    private static final java.lang.String TAG = "nettycon";
//
//    public static final int MSG_PING = 1;
//    public static final int MSG_TIMEOUT = 2;
//    private static final int HAND_SHAKE_KEY_LEN = 24; // 通信用密钥长度,一半由服务器生成,一半由客户端生成
//    private static final long PING_CYCLE = 20000;
//    ChannelFuture con;
//    N n = new N();
//    NettyConnectionHandler nettyConnectionHandler;
//
//    Handler handler;
//    byte[] signKey;
//    final byte[] keyLocal = new byte[HAND_SHAKE_KEY_LEN / 2];
//    L logger = L.get();
//
//
//    public NettyConnection() {
//        Random random = new Random(SystemClock.currentThreadTimeMillis());
//        random.nextBytes(keyLocal);
//    }
//
//    public byte[] getSignKey() {
//        return signKey;
//    }
//
//    public void setSerKey(byte[] serKey) {
//        if (serKey == null || serKey.length != HAND_SHAKE_KEY_LEN / 2) {
//            return;
//        }
//
//        this.signKey = new byte[HAND_SHAKE_KEY_LEN];
//        System.arraycopy(serKey, 0, signKey, 0, serKey.length);
//        System.arraycopy(keyLocal, 0, signKey, HAND_SHAKE_KEY_LEN / 2, keyLocal.length);
//
//    }
//
//    public byte[] getKeyLocal() {
//        return keyLocal;
//    }
//
//    public void connect(String host, int port) throws Exception {
//
//        HandlerThread thread = new HandlerThread("");
//        thread.start();
//        handler = new Handler(thread.getLooper(), this);
//
//        EventLoopGroup group = new NioEventLoopGroup();
//
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group).channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            nettyConnectionHandler = new NettyConnectionHandler(NettyConnection.this);
//                            ch.pipeline()
//                                    .addLast(new ClientPackDecoder())
//                                    .addLast(new ClientPackEncoder())
//                                    .addLast(nettyConnectionHandler);
//                        }
//                    });
//
//            con = b.connect(host, port).sync();
//            con.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully();
//        }
//    }
//
//    public void notifyAllPackFail() {
//        n.notifyAllFailed();
//    }
//
//    public void close() {
//
//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null);
//            handler.getLooper().quit();
//        }
//
//        try {
//            handler.getLooper().getThread().join();
//        } catch (InterruptedException e) {
//            // e.printStackTrace();
//        }
//        if (con != null) {
//            try {
//                con.channel().close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            con = null;
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        int port = 8085;
//        if (args != null && args.length > 0) {
//            port = Integer.valueOf(args[0]);
//        }
//        // new SubReqClient().connect(port, "127.0.0.1");
//    }
//
//    public void sendPack(short cmd, AbstractMessage message, N.NetHandler handler) {
//        if (signKey == null && cmd != CmdIds.GETKEY) {
//            if (handler != null) handler.onFail(ErrorCodes.TRY_AGAIN, null, null);
//            return;
//        }
//
//        Pack pack = null;
//        try {
//            pack = n.request(cmd, message, handler, getSignKey());
//
//            // pack.setSign();
//            nettyConnectionHandler.ctx.write(pack);
//            nettyConnectionHandler.ctx.flush();
//            this.handler.sendEmptyMessageDelayed(MSG_TIMEOUT, Constants.PACK_TIMEOUT + 100);
//        } catch (Exception e) {
//            if(L.E) logger.e(TAG, "", e);
//            if (handler != null) handler.onFail(ErrorCodes.UNKNOWN_ERROR, null, e);
//        }
//
//
//    }
//
//    public abstract void onClosed();
//
//    public void onConnected() {
//        handler.sendEmptyMessageDelayed(MSG_PING, PING_CYCLE);
//    }
//
//
//    public void onReceive(Object msg) throws Exception {
//        if (handler != null) {
//            handler.removeMessages(MSG_PING);
//            handler.sendEmptyMessageDelayed(MSG_PING, PING_CYCLE);
//        }
//
//        Pack pack = (Pack) msg;
//
//        decrypt(pack);
//        n.onReceive(pack);
//    }
//
//    public void decrypt(Pack pack) throws Exception {
//        if (pack.getCommand() != 3 && pack.getData() != null) {
//            pack.setData(Aes256.decrypt(pack.getData(), signKey));
//        }
//    }
//
//    @Override
//    public boolean handleMessage(Message message) {
//        if (message.what == MSG_TIMEOUT) {
//            n.checkTimeout();
//        } else {
//            if (handler != null) {
//                // ping 周期20秒
//                handler.removeMessages(MSG_PING);
//                handler.sendEmptyMessageDelayed(MSG_PING, PING_CYCLE);
//            }
//            sendPack(CmdIds.PING, null, null);
//        }
//
//        return false;
//    }
//}
