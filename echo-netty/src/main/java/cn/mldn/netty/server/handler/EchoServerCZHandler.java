package cn.mldn.netty.server.handler;

import cn.mldn.info.HostInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class EchoServerCZHandler extends ChannelInboundHandlerAdapter {
    /**
     *
     * @param ctx
     * @param msg 是读进来内容，是客户端发送来的信息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String inputData = msg.toString().trim();
            String echoData = "【echo】" + inputData + HostInfo.SEPARATOR;
            ctx.writeAndFlush(echoData);
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
