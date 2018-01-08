//package com.pw.box.core;
//
//
//import com.pw.box.core.bean.Pack;
//import com.pw.box.utils.EncryptUtil;
//
//import java.util.Arrays;
//import java.util.List;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//
///**
// * 客户端数据包解包
// * Created by danger on 16/8/7.
// */
//public class ClientPackDecoder extends ByteToMessageDecoder {
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        while (in.readableBytes() >= Pack.HEADER_LEN) {
//            in.markReaderIndex();
//
//            int packLen = 0xffff & in.readShort();
//
//            if (packLen > C.MAX_CLIENT_PACK_LEN) { // 包体过长视为无效
//                in.clear();
//                return;
//            }
//            if (packLen > in.readableBytes() - (Pack.HEADER_LEN - 2)) { // 共10字切头,已经读取2字节长度
//                in.resetReaderIndex();
//                return;
//            }
//
//            Pack pack = new Pack();
//            pack.setCommand(in.readShort());
//            pack.setReqcode(in.readShort());
//            pack.setCheck(in.readInt());
//
//            if (packLen > 0) {
//                pack.setData(new byte[packLen]);
//                in.readBytes(pack.getData());
//
//                // if ((pack.getCheck() & Pack.BIT_COMPRESSED) != 0) {
//                //     // 数据经过gzip压缩
//                //
//                // }
//
//                EncryptUtil.decrypt(pack.getData(), pack.getCheck());
//                int hash = Arrays.hashCode(pack.getData());
//                if ((hash & 0x7fff) != (pack.getCheck() & 0x7fff)) {
//                    continue; // hash校验不通过,非法包,简单的丢弃
//                }
//            }
//
//            out.add(pack);
//        }
//    }
//}
