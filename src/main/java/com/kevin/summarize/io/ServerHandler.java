package com.kevin.summarize.io;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @类名: ServerHandler
 * @包名：com.kevin.summarize.io
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/14 17:13
 * @版本：1.0
 * @描述：客户端线程，用于处理一个客户端的Socket连接
 */
public class ServerHandler implements Runnable {

    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String expression;
            String result;
            while (true) {
                if ((expression = in.readLine()) == null) {
                    break;
                }
                System.out.println("服务器收到消息: " + expression);
                result = CalculatorUtil.cal(expression);
                out.println(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        } finally {
            if (in == null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }
}
