package com.kevin.summarize.io;

import java.io.IOException;
import java.util.Random;

/**
 * @类名: Test
 * @包名：com.kevin.summarize.io
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/14 22:40
 * @版本：1.0
 * @描述：测试类
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        // 启动服务器
        new Thread(() -> {
            try {
                ServerNormal.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 防止客户端先于服务器启动
        Thread.sleep(100);

        // 启动客户端
        char[] operators = {'+', '-', '*', '/'};
        Random random = new Random(System.currentTimeMillis());
        new Thread(() -> {
            while (true) {
                String expression = random.nextInt(10) + ""
                        + operators[random.nextInt(4)] +
                        (random.nextInt(10) + 1);
                Client.send(expression);
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
