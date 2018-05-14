package com.kevin.summarize.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @类名: ServerNormal
 * @包名：com.kevin.summarize.io
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/14 16:37
 * @版本：1.0
 * @描述：BIO服务端
 */
public class ServerNormal {

    // 默认的端口号
    private static int DEFAULT_PORT = 12345;
    // 单例的ServerSocket
    private static ServerSocket server;

    public static void start() throws IOException {
        start(DEFAULT_PORT);
    }

    public static void start(int port) throws IOException {
        if (server != null) {
            return;
        }
        try {
            server = new ServerSocket(port);
            System.out.println("服务器已启动，端口号: " + port);
            while (true) {
                Socket socket = server.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } finally {
            if (server != null) {
                server.close();
                server = null;
                System.out.println("服务器已关闭");
            }
        }
    }
}
