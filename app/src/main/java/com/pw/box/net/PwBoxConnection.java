//package com.pw.box.net;
//
//
//import com.pw.box.core.ErrorCodes;
//import com.pw.box.core.N;
//import com.pw.box.core.bean.Pack;
//import com.pw.box.core.cmds.CmdIds;
//import com.pw.box.utils.Aes256;
//import com.pw.box.utils.L;
//
///**
// * Created by danger on 2016/12/2.
// */
//
//abstract public class PwBoxConnection extends NioConnection {
//
//    private static final int HAND_SHAKE_KEY_LEN = 24; // 通信用密钥长度,一半由服务器生成,一半由客户端生成
//
//
//    byte[] signKey;
//    final byte[] keyLocal = new byte[HAND_SHAKE_KEY_LEN / 2];
//    L logger = L.get();
//
//
//    N n = new N();
//
//    PackDecoder packDecoder = new PackDecoder() {
//        @Override
//        public void getAPack(Pack pack) {
//            super.getAPack(pack);
//
//            try {
//                decrypt(pack);
//                n.onReceive(pack);
//            } catch (Exception e) {
//                // e.printStackTrace();
//            }
//        }
//    };
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
//            MyByteBuffer buf = new MyByteBuffer(pack.getDataLength() + 30);
//            pack.write(buf);
//            sendData(buf.get(buf.readableBytes()));
//            // nettyConnectionHandler.ctx.write(pack);
//            // nettyConnectionHandler.ctx.flush();
//            // this.handler.sendEmptyMessageDelayed(MSG_TIMEOUT, Constants.PACK_TIMEOUT + 100);
//        } catch (Exception e) {
//            if(L.E) logger.e(TAG, "", e);
//            if (handler != null) handler.onFail(ErrorCodes.UNKNOWN_ERROR, null, e);
//        }
//    }
//
//    @Override
//    public void onIdle() {
//        sendPack(CmdIds.PING, null, null);
//    }
//
//    @Override
//    public void onReceive(MyByteBuffer in) {
//        packDecoder.onReceive(in);
//    }
//
//    public void decrypt(Pack pack) throws Exception {
//        if (pack.getCommand() != 3 && pack.getData() != null) {
//            pack.setData(Aes256.decrypt(pack.getData(), signKey));
//        }
//    }
//}
