package cn.mldn.nio;

import cn.mldn.info.HostInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOEchoServer {
    public static void main(String[] args) throws Exception {
        // 1.NIO的实现要考虑性能问题以及响应时间问题，所以用线程池来控制
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // 2.NIO的处理基于channel控制，Selector负责管理所有的channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3.需要为其设置一个非阻塞的状态机制
        serverSocketChannel.configureBlocking(false);
        // 4.服务器上需提供一个网络监听端口
        serverSocketChannel.bind(new InetSocketAddress(HostInfo.PORT));
        // 5.需要设置一个Selector，作为选择器，管理所有的channel
        Selector selector = Selector.open();
        // 6.将当前的channel注册到selector中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器已经启动成功，服务器的监听端口为：" + HostInfo.PORT);
        // 7.NIO采用轮询模式，每当发现有用户连接的时候，就启动一个线程
        int keySelect = 0;
        while ((keySelect = selector.select()) > 0) { // 实现了轮询处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys:" + selectionKeys.size());
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                if (next.isAcceptable()) {
                    // 为什么这里就能获取到客户端的channel
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    if (clientChannel != null) {
                        executorService.submit(new EchoClientHandler(clientChannel));
                    }
                }
                iterator.remove();
            }
        }
    }

    private static class EchoClientHandler implements Runnable {
        // 客户端通道
        private SocketChannel clientChannel;
        private boolean flag = true;
        public EchoClientHandler(SocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(50);
            try {
                while (this.flag) {
                    // 因为会多次交互，所以每次都要清空缓冲区
                    buffer.clear();
                    // 将数据读入缓冲区中
                    int read = this.clientChannel.read(buffer);
                    String readMessage = new String(buffer.array(), 0, read).trim();
                    String writeMessage = "【echo】" + readMessage + "\n";
                    if ("byebye".equalsIgnoreCase(readMessage)) {
                        writeMessage = "拜拜，下次再见!";
                        this.flag = false;
                    }
                    // 清空缓存，将返回的数据放入
                    buffer.clear();
                    buffer.put(writeMessage.getBytes());
                    buffer.flip();
                    // 回应数据
                    this.clientChannel.write(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
