package cn.mldn.nio;

import cn.mldn.info.HostInfo;
import cn.mldn.util.InputUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOEchoClient {
    public static void main(String[] args) throws Exception {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(HostInfo.HOST_NAME, HostInfo.PORT));
        // 开辟缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(50);
        boolean flag = true;
        while (flag) {
            buffer.clear();
            String inputData = InputUtil.getString("请输入要发送的数据：").trim();
            buffer.put(inputData.getBytes());
            buffer.flip();
            // 发送数据
            clientChannel.write(buffer);
            buffer.clear();
            // 将服务器响应数据读入buffer中
            int readCount = clientChannel.read(buffer);
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, readCount));
            if ("byebye".equalsIgnoreCase(inputData)) {
                flag = false;
            }
        }
    }
}
