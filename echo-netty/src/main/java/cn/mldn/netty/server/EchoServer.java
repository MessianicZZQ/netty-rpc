package cn.mldn.netty.server;

import cn.mldn.info.HostInfo;
import cn.mldn.netty.server.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class EchoServer {
    public void run() throws Exception{
        // netty中线程池的实现分两种： 主线程池、工作线程池
        // 创建接收连接池
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        // 创建工作线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(20);
        System.out.println("服务器启动，监听端口为：" + HostInfo.PORT);
        try {
            // 创建服务器端的程序类，进行NIO启动
            ServerBootstrap serverBootstrap = new ServerBootstrap(); // 服务器端
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            // 定义子处理器
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                    socketChannel.pipeline().addLast(new ObjectEncoder());
                    // 追加了处理器
                    socketChannel.pipeline().addLast(new EchoServerHandler());
                }
            });
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(HostInfo.PORT).sync();
            // 等待socket被关闭
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
