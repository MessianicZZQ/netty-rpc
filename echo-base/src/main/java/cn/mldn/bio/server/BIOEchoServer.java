package cn.mldn.bio.server;

import cn.mldn.info.HostInfo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(HostInfo.PORT);
        System.out.println("服务器端已经启动监听的端口:" + HostInfo.PORT);
        boolean flag = true;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        while (flag) {
            Socket client = serverSocket.accept();
            executorService.submit(new EchoClientHandler(client));
        }
        executorService.shutdown();
        serverSocket.close();
    }

    private static class EchoClientHandler implements Runnable {
        private Socket client; // 每个用户访问都启动一个新的task，即client
        private Scanner scanner;
        private PrintStream out;
        private boolean flag = true;
        public EchoClientHandler (Socket client) {
            this.client = client;
            try {
                this.scanner = new Scanner(this.client.getInputStream());
                // 设置换行符
                this.scanner.useDelimiter("\n");
                this.out = new PrintStream(this.client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            System.out.println("-----");
            while (this.flag) {
                if (this.scanner.hasNext()) {
                    String str = this.scanner.next().trim();
                    if ("byebye".equalsIgnoreCase(str)) {
                        this.out.println("byebyebyebye.....");
                        this.flag = false;
                    } else {
                        this.out.println("【echo】" + str);
                    }
                }
            }
            this.scanner.close();
            this.out.close();
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
