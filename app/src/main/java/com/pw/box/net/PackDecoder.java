package com.pw.box.net;

import com.pw.box.core.C;
import com.pw.box.core.bean.Pack;
import com.pw.box.utils.EncryptUtil;
import com.pw.box.utils.L;

import java.util.Arrays;

/**
 * 解密解码服务器的包
 * Created by danger on 2016/12/2.
 */

public class PackDecoder {

    private final String TAG;

    public PackDecoder(String tag) {
        this.TAG = tag;
    }

    public void onReceive(MyByteBuffer in) {
        while (in.readableBytes() >= Pack.HEADER_LEN) {
            in.markReaderIndex();

            int packLen = 0xffff & in.readShort();

            if (packLen > C.MAX_CLIENT_PACK_LEN) { // 包体过长视为无效
                in.clear();
                if (L.E) L.get().e(TAG, "onReceive error Pack");
                return;
            }
            if (packLen > in.readableBytes() - (Pack.HEADER_LEN - 2)) { // 共10字切头,已经读取2字节长度
                in.resetReaderIndex();

                if (L.E) L.get().e(TAG, "onReceive not enough");
                return;
            }

            Pack pack = new Pack();
            pack.setCommand(in.readShort());
            pack.setReqcode(in.readShort());
            pack.setCheck(in.readInt());

            if (packLen > 0) {
                pack.setData(in.get(packLen));

                // if ((pack.getCheck() & Pack.BIT_COMPRESSED) != 0) {
                //     // 数据经过gzip压缩
                //
                // }

                EncryptUtil.decrypt(pack.getData(), pack.getCheck());
                int hash = Arrays.hashCode(pack.getData());
                if ((hash & 0x7fff) != (pack.getCheck() & 0x7fff)) {
                    continue; // hash校验不通过,非法包,简单的丢弃
                }
            }

            if (L.E) L.get().e(TAG, "onReceive a pack " + pack.getReqcode());
            getAPack(pack);
            // try {
            //     decrypt(pack);
            //     n.onReceive(pack);
            // } catch (Exception e) {
            //     // e.printStackTrace();
            // }
        }
    }

    public void getAPack(Pack pack) {

    }

}
