//package com.pw.box.core;
//
//
//import com.pw.box.core.bean.Pack;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToByteEncoder;
//
///**
// * 客户端数据包的序列化,netty需要
// * Created by danger on 16/8/7.
// */
//public class ClientPackEncoder extends MessageToByteEncoder<Pack> {
//
//    @Override
//    protected void encode(ChannelHandlerContext ctx, Pack msg, ByteBuf out) throws Exception {
//        write(msg, out);
//        ctx.flush();
//    }
//
//    public static void write(Pack msg, ByteBuf out) throws Exception {
//        msg.write(out);
//        /*if (msg.getData() != null && msg.getData().length > C.MAX_CLIENT_PACK_LEN) {
//            throw new Exception("pack too large");
//        }
//
//        out.writeShort(msg.getData() == null ? 0 : msg.getData().length);
//        out.writeShort(msg.getCommand());
//        out.writeShort(msg.getReqcode());
//        out.writeInt(msg.getCheck());
//        // out.writeInt(msg.getCheck());
//        if (msg.getData() != null && msg.getData().length > 0) {
//            out.writeBytes(msg.getData());
//        }*/
//    }
//}
