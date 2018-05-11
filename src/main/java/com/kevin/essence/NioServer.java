package com.kevin.essence;

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
 * @类名: NioServer
 * @包名：com.kevin.essence
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/11 9:58
 * @版本：1.0
 * @描述：
 */
public class NioServer {

    private Selector selector;

    /**
     * 获取一个ServerSocketChannel并将其与selector绑定
     * @param port
     * @return
     * @throws IOException
     */
    public NioServer init(int port) throws IOException {
        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        // 创建Selector
        selector = Selector.open();
        // 将ServerSocketChannel与Selector绑定，并为通道注册ACCEPT事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        return this;
    }

    public void listening() throws IOException {
        System.out.println("server start success");

        // 轮询访问selector
        while (true) {
            // 当有注册的事件到达时，方法返回，否则阻塞
            selector.select();
            // 获取Selector中的迭代器
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println("keys: " + keys.size());
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                // 删除已选key，防止重复处理
//                keyIterator.remove();
                System.out.println("key hashcode: " + key.hashCode() + ", key: " + key.readyOps());
                if (key.isAcceptable()) {
                    keyIterator.remove();
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    // 获得客户端连接通道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    // 向客户端发送消息
                    socketChannel.write(ByteBuffer.wrap(new String("send message to client")
                            .getBytes("UTF-8")));
                    // 与客户端连接成功后，为客户端注册READ事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {  // 有可读数据事件
                    // 获取客户端连接通道
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    int read = socketChannel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data, "UTF-8");
                    System.out.println("receive messasge from client, size: " + buffer.position()
                            + " msg: " + message);
//                    ByteBuffer out = ByteBuffer.wrap(("server.".concat(message)).getBytes("UTF-8"));
//                    socketChannel.write(out);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer().init(8090).listening();
    }
}
