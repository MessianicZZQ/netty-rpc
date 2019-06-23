package cn.mldn.aio;

import cn.mldn.info.HostInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class AIOEchoServer {
    public static void main(String[] args) throws Exception {
        new Thread(new AIOServerThread()).start();
    }
}

// 设置一个单独的服务器处理线程
class AIOServerThread implements Runnable {
    // 服务器通道
    private AsynchronousServerSocketChannel serverChannel = null;
    private CountDownLatch latch = new CountDownLatch(1);
    public AIOServerThread() throws Exception {
        this.serverChannel = AsynchronousServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(HostInfo.PORT));
        System.out.println("服务器启动成功，监听端口为：" + HostInfo.PORT);
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void run() {
        this.serverChannel.accept(this, new AcceptHandler());
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 连接接收的回调处理操作
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AIOServerThread> {

    @Override
    public void completed(AsynchronousSocketChannel channel, AIOServerThread aioThread) {
        // 接收连接
        aioThread.getServerChannel().accept(aioThread, this);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        channel.read(buffer, buffer, new EchoHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AIOServerThread aioThread) {
        System.out.println("客户端连接创建失败。。。");
        aioThread.getLatch().countDown();
    }
}

class EchoHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel clientChannel;
    private boolean exit = false;
    public EchoHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        String readMessage = new String(buffer.array(), 0, buffer.remaining()).trim();
        String writeMessage = "【echo】" + readMessage;
        if ("byebye".equalsIgnoreCase(readMessage)) {
            writeMessage = "拜拜，下次再见！";
            this.exit = true;
        }
        this.echoWrite(writeMessage);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void echoWrite(String content) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put(content.getBytes());
        buffer.flip();
        this.clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {
                    EchoHandler.this.clientChannel.write(buf, buf, this);
                } else {
                    if (!EchoHandler.this.exit) { // 说明还没有结束
                        ByteBuffer readBuffer = ByteBuffer.allocate(100);
                        EchoHandler.this.clientChannel.read(readBuffer, readBuffer, new EchoHandler(EchoHandler.this.clientChannel));
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    EchoHandler.this.clientChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
