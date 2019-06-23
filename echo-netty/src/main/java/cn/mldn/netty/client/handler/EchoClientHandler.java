package cn.mldn.netty.client.handler;

import cn.mldn.vo.Member;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Member member = new Member("xiaoli", "小李老师", 16, 1.1);
        System.out.println(member);
        ctx.writeAndFlush(member);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 服务器发送了消息后，都会进去客户端的这个方法
        try{
            Member member = (Member)msg;
            System.out.println("member:" + member);
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
