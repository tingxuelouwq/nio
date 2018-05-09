package com.kevin.socketchannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

/**
 * @类名: ClientMain
 * @包名：com.kevin
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/9 9:40
 * @版本：1.0
 * @描述：
 */
public class ClientMain {

    private static final int sleepTime = 1000*1000*1000;

    public static void main(String[] args) {
        Socket client = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            client = new Socket();
            client.connect(new InetSocketAddress("localhost", 8000));
            writer = new PrintWriter(client.getOutputStream(), true);
            writer.print("H");
            LockSupport.parkNanos(sleepTime);
            writer.print("e");
            LockSupport.parkNanos(sleepTime);
            writer.print("l");
            LockSupport.parkNanos(sleepTime);
            writer.print("l");
            LockSupport.parkNanos(sleepTime);
            writer.print("o");
            LockSupport.parkNanos(sleepTime);
            writer.print("!");
            LockSupport.parkNanos(sleepTime);
            writer.println();
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("from server: " + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
