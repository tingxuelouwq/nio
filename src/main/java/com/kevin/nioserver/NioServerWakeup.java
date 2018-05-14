package com.kevin.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @类名: NioServerWakeup
 * @包名：com.kevin.essence
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/14 15:06
 * @版本：1.0
 * @描述：
 */
public class NioServerWakeup {

    private Selector selector;
    private Selector selector2;

    public NioServerWakeup init(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        return this;
    }

    public void listening() throws IOException {
        System.out.println("server start success");
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println(Thread.currentThread().getName() + ", keys: " + keys.size());
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                System.out.println(Thread.currentThread().getName() + ", key hashcode: " + key.hashCode() + ", key: " + key.readyOps());
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.write(ByteBuffer.wrap(new String("send message to client")
                            .getBytes("UTF-8")));
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    int read = socketChannel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data, "UTF-8");
                    System.out.println(Thread.currentThread().getName() + ", receive message from client, size:"
                            + buffer.position() + " msg: " + message);
//                    ByteBuffer out = ByteBuffer.wrap(("server.".concat(message)).getBytes("UTF-8"));
//                    socketChannel.write(out);

                    socketChannel.register(selector, 0);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    new Thread(() -> {
                        try {
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            key.selector().wakeup();
                        } catch (ClosedChannelException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServerWakeup().init(9010).listening();
    }
}
