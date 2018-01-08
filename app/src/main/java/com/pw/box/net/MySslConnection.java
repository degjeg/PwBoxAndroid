package com.pw.box.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.common.bean.DataWithLen;
import com.common.bean.GetCommunicateKeyRequest;
import com.common.bean.GetCommunicateKeyResponse;
import com.common.bean.GetPublicKeyRequest;
import com.common.bean.GetPublicKeyResponse;
import com.pw.box.App;
import com.pw.box.cache.Constants;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.K;
import com.pw.box.core.N;
import com.pw.box.core.PacketCreator;
import com.pw.box.core.bean.Pack;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.utils.Aes256;
import com.pw.box.utils.L;
import com.pw.box.utils.PrefUtil;
import com.pw.box.utils.RsaUtils;
import com.squareup.wire.Message;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okio.ByteString;

/**
 * 长连接
 * Created by danger on 2016/12/2.
 */

public class MySslConnection extends NioConnection {

    private static final int HAND_SHAKE_KEY_LEN = 32; // 通信用密钥长度,一半由服务器生成,一半由客户端生成

    final List<ConnectionListener> listeners = new ArrayList<>();
    final byte[] keyHandshake1 = new byte[HAND_SHAKE_KEY_LEN];
    final byte[] keyHandshake2 = new byte[HAND_SHAKE_KEY_LEN];
    final String ACCOUNT;
    List<byte[]> signKey; // = new byte[HAND_SHAKE_KEY_LEN];
    byte[] keyLocal; //  = new byte[HAND_SHAKE_KEY_LEN];
    L logger = L.get();
    String pubKey = null;
    String pubExponent = null;
    int pubKeyVer = -1;
    SecureRandom random = new SecureRandom();
    N n;
    PackDecoder packDecoder;
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean sslConnected = false;

    public MySslConnection(String account) {
        super();
        this.ACCOUNT = account;

        random.nextBytes(keyHandshake1);
        random.nextBytes(keyHandshake2);
        n = new N(TAG);

        packDecoder = new PackDecoder(TAG) {
            @Override
            public void getAPack(Pack pack) {
                super.getAPack(pack);

                try {
                    if (pack.getCommand() == CmdIds.GET_PUBLICK_KEY) {
                        handleGetPubKey(pack);
                    } else if (pack.getCommand() == CmdIds.GET_COM_KEY) {
                        handleGetComKey(pack);
                    } else {
                        decrypt(pack);
                        notifyReceive(pack);
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        };
    }

    private void reqGetPubKey() {
        pubKey = PrefUtil.getString(App.getContext(), Constants.PREF_KEY_PUBKEY, "");
        pubExponent = PrefUtil.getString(App.getContext(), Constants.PREF_KEY_PUBKEY_E, "");

        // BigInteger i = TextUtils.isEmpty(pubKey) ? BigInteger.valueOf(0) : new BigInteger(pubKey);
        pubKeyVer = PrefUtil.getInt(App.getContext(), Constants.PREF_KEY_PUBKEY_V, 0);

        GetPublicKeyRequest req = new GetPublicKeyRequest.Builder()
                .random1(ByteString.of(keyHandshake1))
                .account(ACCOUNT)
                .ver(pubKeyVer)
                .build();

        Pack pack = null;
        try {
            byte[] content = req.encode();
            content = Aes256.encrypt(content, K.k1());

            int len = content.length;
            content = RsaUtils.encryptByPublicKey(content, K.r());

            content = new DataWithLen.Builder().len(len).data(okio.ByteString.of(content)).build().encode();


            pack = PacketCreator.createPack(CmdIds.GET_PUBLICK_KEY, N.reqID(), content, null); //
            MyByteBuffer buf = new MyByteBuffer(pack.getDataLength() + 30);
            pack.write(buf);
            sendData(buf.get(buf.readableBytes()));
        } catch (Exception e) {
            if (L.E) logger.e(TAG, "", e);
            notifyClose(e);
        }
    }

    private void handleGetPubKey(Pack pack) {
        try {
            byte[] rawData = pack.getData();
            if (L.E) L.get().e(TAG, "\n密钥1:\n" + Arrays.toString(keyHandshake1));
            if (L.E) L.get().e(TAG, "\n解密前:\n" + Arrays.toString(rawData));
            rawData = Aes256.decrypt(rawData, keyHandshake1);
            if (L.E) L.get().e(TAG, "\n第一次解密后:\n" + Arrays.toString(rawData));

            rawData = Aes256.decrypt(rawData, K.k1());
            if (L.E) L.get().e(TAG, "密钥2" + Arrays.toString(K.k1()));
            if (L.E) L.get().e(TAG, "\n第二次解密后:\n" + Arrays.toString(rawData));


            GetPublicKeyResponse response = GetPublicKeyResponse.ADAPTER.decode(rawData);
            if (response.ret_code == ErrorCodes.SUCCESS) {
                if (response.ver != this.pubKeyVer || TextUtils.isEmpty(this.pubKey)) {
                    pubKey = new BigInteger(response.public_key.toByteArray()).toString();
                    pubExponent = new BigInteger(response.pub_exponent.toByteArray()).toString();

                    PrefUtil.setInt(App.getContext(), Constants.PREF_KEY_PUBKEY_V, response.ver);
                    PrefUtil.setString(App.getContext(), Constants.PREF_KEY_PUBKEY, pubKey);
                    PrefUtil.setString(App.getContext(), Constants.PREF_KEY_PUBKEY_E, pubExponent);
                }
                reqGetComKey(response.comkey_len);
            } else {
                notifyClose(null);
            }
        } catch (Exception e) {
            if (L.E) logger.e(TAG, "", e);
            notifyClose(e);
        }
    }


    private void reqGetComKey(int keyLen) {
        try {
            keyLocal = new byte[keyLen];
            random.nextBytes(keyLocal);

            GetCommunicateKeyRequest req = new GetCommunicateKeyRequest.Builder()
                    .random1(okio.ByteString.of(keyLocal))
                    .random2(okio.ByteString.of(keyHandshake2))
                    .build();


            byte[] content = req.encode();
            content = Aes256.encrypt(content, K.k2());
            int len = content.length;
            content = RsaUtils.encryptByPublicKey(content, RsaUtils.getPublicKey(pubExponent, pubKey));
            content = new DataWithLen.Builder().len(len).data(okio.ByteString.of(content)).build().encode();

            Pack pack = PacketCreator.createPack(CmdIds.GET_COM_KEY, N.reqID(), content, null);
            MyByteBuffer buf = new MyByteBuffer(pack.getDataLength() + 30);
            pack.write(buf);
            sendData(buf.get(buf.readableBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            notifyClose(e);
        }
    }

    private void handleGetComKey(Pack pack) {
        try {
            if (sslConnected) {
                return;
            }
            byte[] rawData = pack.getData();
            rawData = Aes256.decrypt(rawData, keyHandshake2);
            rawData = Aes256.decrypt(rawData, K.k2());
            GetCommunicateKeyResponse response = GetCommunicateKeyResponse.ADAPTER.decode(rawData);

            if (response.ret_code == ErrorCodes.SUCCESS) {
                byte[] keyServer = response.random1.toByteArray(); // Aes256.decrypt(response.random1.toByteArray(), keyHandshake1);
                signKey = new ArrayList<>();
                for (int i = 0; i < keyLocal.length; i += 32) {
                    int len = Math.min(keyLocal.length - i, 32);
                    byte[] k = new byte[len];
                    signKey.add(k);
                    for (int j = 0; j < len; j++) {
                        k[j] = (byte) (keyLocal[i + j] ^ keyServer[i + j]);
                    }
                }

                // L.get().e(TAG, "realKey:" + Arrays.toString(signKey));
                sslConnected = true;
                notifyConected();
            } else {
                notifyClose(null);
            }
        } catch (Exception e) {
            if (L.E) logger.e(TAG, "", e);
            notifyClose(e);
        }
    }


    public void init(String host, int port) {
        super.init(new LocalListener(), host, port);
        super.packDecoder = this.packDecoder;
    }

    public void addConnectionListener(ConnectionListener listener) {
        synchronized (listeners) {
            this.listeners.remove(listener);
            this.listeners.add(listener);
        }
        if (sslConnected) {
            listener.onConnected();
        }
    }

    public void removeConnectionListener(ConnectionListener listener) {
        synchronized (listeners) {
            this.listeners.remove(listener);
        }
    }

    // public Pack sendPack(short cmd, AbstractMessage message) {
    //     return sendPack(cmd, message == null ? null : message.toByteArray());
    // }

    public Pack sendPack(short cmd, Message message) {
        return sendPack(cmd, message == null ? null : message.encode());
    }

    public Pack sendPack(short cmd, byte[] message) {
        Pack pack = null;
        try {
            pack = PacketCreator.createPack(cmd, N.reqID(), message, signKey); //  n.request(cmd, message, handler, signKey);
            MyByteBuffer buf = new MyByteBuffer(pack.getDataLength() + 30);
            pack.write(buf);
            sendData(buf.get(buf.readableBytes()));
            return pack;
        } catch (Exception e) {
            if (L.E) logger.e(TAG, "", e);
            // if (handler != null) handler.onFail(ErrorCodes.UNKNOWN_ERROR, null, e);
        }
        return null;
    }

    public void decrypt(Pack pack) throws Exception {
        if (pack.getCommand() != 3 && pack.getData() != null) {
            pack.setData(Aes256.decrypt(pack.getData(), signKey));
        }
    }

    public String getAccount() {
        return ACCOUNT;
    }

    private void notifyConected() {
        final List<ConnectionListener> LISTENERS = new ArrayList<>(this.listeners);
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener oneListener : LISTENERS) {
                    try {
                        oneListener.onConnected();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        });

    }

    private void notifyIdle() {
        final List<ConnectionListener> LISTENERS = new ArrayList<>(this.listeners);
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener l : LISTENERS) {
                    try {
                        l.onIdle();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        });
    }

    private void notifyReceive(final Pack pack) {
        final List<ConnectionListener> LISTENERS = new ArrayList<>(this.listeners);
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener l : LISTENERS) {
                    try {
                        l.onReceive(pack);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        });
    }

    private void notifyClose(final Exception e) {
        sslConnected = false;
        final List<ConnectionListener> LISTENERS = new ArrayList<>(this.listeners);
        this.listeners.clear(); // 连接关闭只需要通知一次

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener l : LISTENERS) {
                    try {
                        l.onClosed(e);
                    } catch (Exception e1) {
                        // e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public String toString() {
        return "con:" + id + ACCOUNT;
    }

    private class LocalListener implements ConnectionListener {
        @Override
        public void onIdle() {
            if (!sslConnected) {
                if (L.E) L.get().e(TAG, "ssl connect timeout by idle");
                notifyClose(null);
                close();
                return;
            }

            sendPack(CmdIds.PING, (byte[]) null);
            notifyIdle();
        }


        @Override
        public void onConnected() {
            // synchronized (listeners) {
            //     for (ConnectionListener l : listeners) {
            //         l.onConnected();
            //     }
            // }
            reqGetPubKey();
        }

        @Override
        public void onClosed(Exception e) {
            notifyClose(e);
        }

        @Override
        public void onReceive(Pack pack) {
            notifyReceive(pack);
        }
    }
}
