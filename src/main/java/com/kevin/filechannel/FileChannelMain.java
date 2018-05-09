package com.kevin.filechannel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.BiConsumer;

/**
 * @类名: FileChannelMain
 * @包名：com.kevin
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/8 15:01
 * @版本：1.0
 * @描述：
 */
public class FileChannelMain {

    public static void main(String[] args) throws IOException {
        String src = "E:\\迅雷下载\\mysql-5.7.20-linux-glibc2.12-x86_64.tar.gz";
        String des = "E:\\迅雷下载\\test.rar";
//        benchmark(FileChannelMain::nioCopyFile, src, des);
//        benchmark(FileChannelMain::ioCopyFile, src, des);
//        benchmark(FileChannelMain::fileTransferTo, src, des);
    }

    /**
     * transfer文件，平均耗时0.0s
     * @param src
     * @param des
     */
    public static void fileTransferTo(String src, String des) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel readChannel = null;
        FileChannel writeChannel = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            readChannel = fis.getChannel();
            writeChannel = fos.getChannel();
            readChannel.transferTo(0, readChannel.size(), writeChannel);
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

    /**
     * nio复制文件，平均耗时2.0s
     * @param src
     * @param des
     * @throws IOException
     */
    public static void nioCopyFile(String src, String des) {
        FileInputStream fis;
        FileOutputStream fos;
        FileChannel readChannel = null;
        FileChannel writeChannel = null;
        ByteBuffer buffer;
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

    /**
     * io复制文件，平均耗时1.0s
     * @param src
     * @param des
     */
    public static void ioCopyFile(String src, String des) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void benchmark(BiConsumer<String, String> consumer, String src, String des) {
        long start = System.currentTimeMillis();
        consumer.accept(src, des);
        double diff = System.currentTimeMillis() - start;
        System.out.println("cost " + diff + " ms");
    }
}
