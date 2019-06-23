package cn.mldn.netty.client;

import cn.mldn.info.HostInfo;
import cn.mldn.netty.client.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class EchoClient {
    public void run () throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建客户端的处理程序
            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)// 允许大块的返回数据
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture future = client.connect(HostInfo.HOST_NAME, HostInfo.PORT).sync();
//            future.addListener(new GenericFutureListener() {
//                public void operationComplete(Future future) throws Exception {
//                    if (future.isSuccess()) {
//                        System.out.println("已经连接成功，可以进行准确的数据传输");
//                    }
//                }
//            });
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
