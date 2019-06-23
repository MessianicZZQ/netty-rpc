package cn.mldn.netty.server.handler;

import cn.mldn.vo.Member;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    /**
     *
     * @param ctx
     * @param msg 是读进来内容，是客户端发送来的信息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 表示要进行消息的读取操作，读取完成后也可以直接回应
            Member member = (Member)msg;
            System.out.println("服务器:" + member);
            member.setName("【echo】" + member.getName());
            ctx.writeAndFlush(member);
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
