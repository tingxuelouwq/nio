package com.kevin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.BiConsumer;

/**
 * @类名: Main
 * @包名：com.kevin
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/8 15:01
 * @版本：1.0
 * @描述：
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String src = "E:\\迅雷下载\\mysql-5.7.20-linux-glibc2.12-x86_64.tar.gz";
        String des = "E:\\迅雷下载\\test.rar";
        benchmark(Main::nioCopyFile, src, des);
    }

    /**
     * nio复制文件
     * @param src
     * @param des
     * @throws IOException
     */
    public static void nioCopyFile(String src, String des) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel readChannel = null;
        FileChannel writeChannel = null;
        ByteBuffer buffer = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            readChannel = fis.getChannel();
            writeChannel = fos.getChannel();
            buffer = ByteBuffer.allocate(1024);
            while (readChannel.read(buffer) != -1) {
                buffer.flip();
                writeChannel.write(buffer);
                buffer.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                readChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writeChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ioCopyFile(String src, String des) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
        }
    }

    public static void benchmark(BiConsumer<String, String> consumer, String src, String des) {
        long start = System.currentTimeMillis();
        consumer.accept(src, des);
        double diff = (System.currentTimeMillis() - start) / 1000;
        System.out.println("cost " + diff + " s");
    }
}
