package cn.mldn.netty.client.handler;

import cn.mldn.info.HostInfo;
import cn.mldn.util.InputUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class EchoClientCZHandler extends ChannelInboundHandlerAdapter {
    private static final int REPEAT = 50;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < REPEAT; i++) {
            String data = "【" + i + "】" + " hello" + HostInfo.SEPARATOR;
            ctx.writeAndFlush(data);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 服务器发送了消息后，都会进去客户端的这个方法
        try{
            // 得到服务器端返回的内容
            String readData = msg.toString().trim();
            System.out.println(readData);
        } finally {
            // 释放缓存
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
