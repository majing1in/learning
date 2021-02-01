package com.xiaoma.nio.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 0:12
 * @Email: 1468835254@qq.com
 * @Description: 最基本的消息发送接收 Demo
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        // 获取一个 ServerSocketChannel 对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 获取一个 Selector 对象
        Selector selector = Selector.open();
        // 绑定端口号
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 将 selector 注册到 ServerSocketChannel中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 没有事件发生
            if (selector.select(1000) == 0) {
                System.out.println(" Server wait one second, no connections！");
                continue;
            }
            // 有事件发生，获取发生事件的 selectionKey
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // 获取 key
                SelectionKey key = iterator.next();
                // 获取事件类型
                if (key.isValid() && key.isAcceptable()) {
                    // 生成客户端 SocketChannel,这一步并不会阻塞（事件驱动体现之一）
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    // SocketChannel 关注事件,并绑定一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (key.isValid() && key.isReadable()) {
                    // 通过 key 获取 channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 获取 channel 关联的 buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println(" Server send data ===> " + new String(buffer.array()));
                    channel.close();
                }
                // 移除 iterator 防止重复操作
                iterator.remove();
            }
        }
    }
}
