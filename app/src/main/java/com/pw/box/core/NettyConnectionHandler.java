//package com.pw.box.core;
//
//import com.pw.box.utils.L;
//
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//
//public class NettyConnectionHandler extends ChannelHandlerAdapter {
//
//    L logger = L.get();
//    // private static final Logger logger = Logger.getLogger(SubReqClientHandler.class.getName());
//
//    public ChannelHandlerContext ctx;
//    NettyConnection nettyConnection;
//
//    public NettyConnectionHandler(NettyConnection nettyConnection) {
//        this.nettyConnection = nettyConnection;
//    }
//
//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelRegistered(ctx);
//        this.ctx = ctx;
//    }
//
//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelUnregistered(ctx);
//
//        nettyConnection.onClosed();
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        this.ctx = ctx;
//        nettyConnection.onConnected();
//
////        N.get().
//        // for (int i = 0; i < 10; i++) {
//        //     ctx.write(req(i));
//        // }
//        // ctx.flush();
//    }
//
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);
//        ctx.flush();
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg)
//            throws Exception {
//        nettyConnection.onReceive(msg);
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
//            throws Exception {
//        if(L.E) logger.e("", "", cause);
//        // if(L.E) logger.warning("unexpected exception from downstream:" + cause.getMessage());
//        ctx.close();
//    }
//}
