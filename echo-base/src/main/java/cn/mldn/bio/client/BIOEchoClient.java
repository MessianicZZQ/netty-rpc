package cn.mldn.bio.client;

import cn.mldn.info.HostInfo;
import cn.mldn.util.InputUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class BIOEchoClient {
    public static void main(String[] args) throws Exception {
        // 定义要连接的主机信息
        Socket client = new Socket(HostInfo.HOST_NAME, HostInfo.PORT);
        // 获取服务器的响应数据
        Scanner scanner = new Scanner(client.getInputStream());
        scanner.useDelimiter("\n");
        // 向服务器发送信息
        PrintStream out = new PrintStream(client.getOutputStream());
        boolean flag = true;
        while (flag) {
            String inputData = InputUtil.getString("请输入要发送的内容：").trim();
            // 把数据发送到服务器
            out.println(inputData);
            if (scanner.hasNext()) {
                String str = scanner.next().trim();
                System.out.println(str);
            }
            if ("byebye".equalsIgnoreCase(inputData)) {
                flag = false;
            }
        }
        client.close();
    }
}
