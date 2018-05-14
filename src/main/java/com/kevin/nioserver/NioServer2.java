package com.kevin.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @类名: NioServer2
 * @包名：com.kevin.essence
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/14 9:51
 * @版本：1.0
 * @描述：
 */
public class NioServer2 {

    private Selector selector;
    private Selector selector2;

    public NioServer2 init(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        selector = Selector.open();
        selector2 = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        return this;
    }

    public void listening() throws IOException {
        System.out.println("server start success");
        while (true) {
            // 当有注册的事件到达时，方法返回，否则阻塞
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println("keys: " + keys.size());
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                // 删除已选key，防止重复处理
//                keyIterator.remove();
                System.out.println("key hashcode: " + key.hashCode() + ", key: " + key.readyOps());
                System.out.println("channnel hashcode: " + key.channel().hashCode());
                System.out.println("selector hashcode: " + key.selector().hashCode());
                if (key.isAcceptable()) {
                    keyIterator.remove();
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.write(ByteBuffer.wrap(new String("send message to client")
                            .getBytes("UTF-8")));
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    socketChannel.register(selector2, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    int read = socketChannel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data, "UTF-8");
                    System.out.println("receive message from client, size:"
                            + buffer.position() + " msg: " + message);
//                    ByteBuffer out = ByteBuffer.wrap(("server.".concat(message)).getBytes("UTF-8"));
//                    socketChannel.write(out);
                }
            }

            selector2.select();
            Set<SelectionKey> keys2 = selector2.selectedKeys();
            System.out.println("keys2: " + keys2.size());
            Iterator<SelectionKey> keyIterator2 = keys2.iterator();
            while (keyIterator2.hasNext()) {
                SelectionKey key2 = keyIterator2.next();
                // 删除已选key，防止重复处理
//                keyIterator.remove();
                System.out.println("key2 hashcode : " + key2.hashCode() + ", key2: " + key2.readyOps());
                System.out.println("channnel2 hashcode: " + key2.channel().hashCode());
                System.out.println("selector2 hashcode: " + key2.selector().hashCode());
                if (key2.isAcceptable()) {
                    keyIterator.remove();
                    ServerSocketChannel serverSocketChannel2 = (ServerSocketChannel) key2.channel();
                    SocketChannel socketChannel2 = serverSocketChannel2.accept();
                    socketChannel2.configureBlocking(false);
                    socketChannel2.write(ByteBuffer.wrap(new String("send message to client")
                            .getBytes("UTF-8")));
                    socketChannel2.register(selector, SelectionKey.OP_READ);
                } else if (key2.isReadable()) {
                    SocketChannel socketChannel2 = (SocketChannel) key2.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    int read = socketChannel2.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data, "UTF-8");
                    System.out.println("receive message from client, size:"
                            + buffer.position() + " msg: " + message);
//                    ByteBuffer out = ByteBuffer.wrap(("server.".concat(message)).getBytes("UTF-8"));
//                    socketChannel.write(out);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer2().init(9000).listening();
    }
}
