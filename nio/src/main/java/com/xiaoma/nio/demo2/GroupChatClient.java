package com.xiaoma.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 14:38
 * @Email: 1468835254@qq.com
 */
public class GroupChatClient {

    private static final String Host = "127.0.0.1";
    private static final int PORT = 6667;

    private Selector selector;
    private SocketChannel socketChannel;

    private String clientName;

    public GroupChatClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(Host, PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            clientName = socketChannel.getLocalAddress().toString().substring(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Client init failed! ");
        }
    }

    private void sendToServer(String message) {
        message = this.clientName + ":" + message;
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromServer() {
        try {
            int select = selector.select(1000);
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey type = iterator.next();
                    if (type.isReadable()) {
                        SocketChannel channel = (SocketChannel) type.channel();
                        channel.configureBlocking(false);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        channel.read(byteBuffer);
                        String message = new String(byteBuffer.array());
                        System.out.println(message);
                    }
                    iterator.remove();
                }
            } else {
                // TODO
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatClient chatClient = new GroupChatClient();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    chatClient.readFromServer();
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            chatClient.sendToServer(line);
        }
    }
}
