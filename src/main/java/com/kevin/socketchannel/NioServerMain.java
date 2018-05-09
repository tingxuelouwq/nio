package com.kevin.socketchannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @类名: NioServerMain
 * @包名：com.kevin.socketchannel.nio
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/9 10:06
 * @版本：1.0
 * @描述：
 */
public class NioServerMain {

    private static Map<Socket, Long> map = new HashMap<>();
    private Selector selector;
    private ExecutorService service = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        NioServerMain nioServer = new NioServerMain();
        try {
            nioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {
        selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(8000));
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        for (; ; ) {
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = readyKeys.iterator();
            long start = 0;
            long end = 0;
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    doAccept(key);
                } else if (key.isValid() && key.isReadable()) {
                    if (!map.containsKey(((SocketChannel) key.channel()).socket())) {
                        map.put(((SocketChannel) key.channel()).socket(), System.currentTimeMillis());
                    }
                    doRead(key);
                } else if (key.isValid() && key.isWritable()) {
                    doWrite(key);
                    end = System.currentTimeMillis();
                    start = map.remove(((SocketChannel)key.channel()).socket());
                    System.out.println(Thread.currentThread() + " spend: " + (end - start) + " ms");
                }
            }
        }
    }

    private void doWrite(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        EchoClient echoClient = (EchoClient) key.attachment();
        LinkedList<ByteBuffer> queue = echoClient.getQueue();
        ByteBuffer buffer = queue.getLast();
        try {
            if (socketChannel.write(buffer) != -1) {
                if (buffer.remaining() == 0) {
                    queue.removeLast();
                }
            } else {
                disconnect(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(key);
        }
        if (queue.size() == 0) {
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void doRead(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        try {
            if (channel.read(buffer) != -1) {
                buffer.flip();
                service.execute(new HandleMsg(key, buffer));
            } else {
                disconnect(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(key);
        }
    }

    private void disconnect(SelectionKey key) {
        try {
            key.channel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doAccept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel;
        try {
            clientChannel = serverSocketChannel.accept();
            clientChannel.configureBlocking(false);
            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
            EchoClient echoClient = new EchoClient();
            clientKey.attach(echoClient);
            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from " + clientAddress.getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class HandleMsg implements Runnable {

        private SelectionKey key;
        private ByteBuffer buffer;

        public HandleMsg(SelectionKey key, ByteBuffer buffer) {
            this.key = key;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            EchoClient echoClient = (EchoClient) key.attachment();
            echoClient.enqueue(buffer);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            selector.wakeup();
        }
    }

    private class EchoClient {
        private LinkedList<ByteBuffer> queue;

        public EchoClient() {
            this.queue = new LinkedList<>();
        }

        public void enqueue(ByteBuffer buffer) {
            queue.addFirst(buffer);
        }

        public LinkedList<ByteBuffer> getQueue() {
            return queue;
        }
    }
}
