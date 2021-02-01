package com.xiaoma.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 14:08
 * @Email: 1468835254@qq.com
 */
public class GroupChatServer {

    // 定义属性
    private Selector selector;
    private ServerSocketChannel listener;
    private static final int PORT = 6667;

    // 初始化
    public GroupChatServer() {
        try {
            selector = Selector.open();
            listener = ServerSocketChannel.open();
            listener.socket().bind(new InetSocketAddress(PORT));
            listener.configureBlocking(false);
            listener.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 监听各种事件
    public void Listen() {
        while (true) {
            try {
                int count = selector.select(1000);
                if (count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey type = iterator.next();
                        // 监听 accept
                        if (type.isAcceptable()) {
                            SocketChannel socketChannel = listener.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + "以上线");
                        }
                        // 监听 read
                        if (type.isReadable()) {
                            this.readData(type);
                        }
                        iterator.remove();
                    }
                } else {
                    System.out.println(" Waiting~~~ ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readData(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            // 读取到数据
            if (count > 0) {
                String message = new String(buffer.array());
                System.out.println(channel.getRemoteAddress() + ":" + message);
                this.sendToClients(message, channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
            key.channel();
        }
    }

    private void sendToClients(String message, SocketChannel myself) {
        System.out.println(" Server send message ~~~ ");
        // 遍历所有 socketChannel
        selector.keys().forEach(key -> {
            try {
                Channel channel = key.channel();
                if (channel instanceof SocketChannel && Objects.equals(channel, myself)) {
                    SocketChannel socketChannel = (SocketChannel) channel;
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                    socketChannel.write(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        GroupChatServer server = new GroupChatServer();
        server.Listen();
    }
}
