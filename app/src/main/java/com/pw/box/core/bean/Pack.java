package com.pw.box.core.bean;


import com.pw.box.core.C;
import com.pw.box.net.MyByteBuffer;
import com.squareup.wire.Message;

/**
 * 服务器和客户端进行通信的数据包
 * Created by danger on 16/8/6.
 */
public class Pack {
    public static final int HEADER_LEN = 10;

    public static final int BIT_COMPRESSED = 0x8000;
    short dataLength;
    short command;
    short reqcode;

    /**
     * bit31 标识数据是否压缩
     * bit30-bit16 预留,暂时未使用
     * bit15-bit0存储原始内容的hash低16位用于校验和解密
     */
    int check; // 数据校验
    byte[] sign; // sign,定长8字节
    /* 以上内容共10个字节包头 */

    byte data[];

    public Pack() {
    }

    public Pack(Message message) {
        if (message != null) {
            data = message.encode();
        }
    }

    public short getCommand() {
        return command;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    public short getReqcode() {
        return reqcode;
    }

    public void setReqcode(short reqcode) {
        this.reqcode = reqcode;
    }

    public short getDataLength() {
        return (short) (data == null ? 0 : data.length);
    }

    public void setDataLength(short dataLength) {
        this.dataLength = dataLength;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }

    //    public void write(ByteBuf out) throws Exception {
    //        if (getData() != null && getData().length > C.MAX_CLIENT_PACK_LEN) {
    //            throw new Exception("pack too large");
    //        }
    //
    //        out.writeShort(getData() == null ? 0 : getData().length);
    //        out.writeShort(getCommand());
    //        out.writeShort(getReqcode());
    //        out.writeInt(getCheck());
    //        // out.writeInt(msg.getCheck());
    //        if (getData() != null && getData().length > 0) {
    //            out.writeBytes(getData());
    //        }
    //    }

    public void write(MyByteBuffer out) throws Exception {
        if (getData() != null && getData().length > C.MAX_CLIENT_PACK_LEN) {
            throw new Exception("pack too large");
        }

        out.writeShort(getData() == null ? 0 : getData().length);
        out.writeShort(getCommand());
        out.writeShort(getReqcode());
        out.writeInt(getCheck());
        // out.writeInt(msg.getCheck());
        if (getData() != null && getData().length > 0) {
            out.writeBytes(getData());
        }
    }
}
