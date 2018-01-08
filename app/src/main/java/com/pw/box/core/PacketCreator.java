package com.pw.box.core;


import com.pw.box.core.bean.Pack;
import com.pw.box.utils.Aes256;
import com.pw.box.utils.EncryptUtil;
import com.pw.box.utils.GZIPUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 将数据打成服务器识别的包
 * Created by danger on 16/8/7.
 */
public class PacketCreator {

    // public static Pack createPack(short command, short reqCode, Message message, List<byte[]> signKey) throws Exception {
    //     return createPack(command, reqCode, message == null ? null : message.encode(), signKey);
    // }

    public static Pack createPack(short command, short reqCode, com.squareup.wire.Message message, List<byte[]> signKey) throws Exception {
        return createPack(command, reqCode, message == null ? null : message.encode(), signKey);
    }

    public static Pack createPack(short command, short reqCode, byte[] message, List<byte[]> signKey) throws Exception {
        Pack pack = new Pack();
        pack.setCommand(command); // 命令
        pack.setReqcode(reqCode); // 请求编号

        if (message == null) {
            return pack;
        }

        byte data[] = message; // message.toByteArray();

        byte[] compressedData = tryToCompressData(data);
        boolean compressed = compressedData != data;
        if (signKey != null) {
            compressedData = Aes256.encrypt(compressedData, signKey);
        }
        pack.setData(compressedData); // 数据

        int hash = Arrays.hashCode(compressedData);
        pack.setCheck(0x7fff & hash);
        if (compressed) {
            pack.setCheck(pack.getCheck() | pack.BIT_COMPRESSED); // 标识数据已压缩
        }
        EncryptUtil.encrypt(compressedData, pack.getCheck());

        return pack;
    }

    public static byte[] tryToCompressData(byte[] data) {
        if (data.length < C.COMPRESS_TRIGGER) {
            return data;
        }

        byte[] compressedData = null;
        try {
            compressedData = GZIPUtil.gZIPCompress(data);
        } catch (IOException e) {
            // e.printStackTrace();
        }

        if (compressedData == null
                || (compressedData.length > data.length * 2 / 3 // 大于原始数据2/3
                && data.length - compressedData.length < 500 // 数据减少小于500
        )) {
            // 放弃压缩
            return data;
        }

        return compressedData;
    }
}
