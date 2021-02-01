package com.xiaoma.nio.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 0:36
 * @Email: 1468835254@qq.com
 */
public class NIOClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 获取通道
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // 建立连接
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 6666);
        if (!socketChannel.connect(socketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println(" Link ~~~ ");
            }
        }
        String data = " Test Nio！";
        // 产生一个字节数组到 buffer 中
        ByteBuffer byteBuffer = ByteBuffer.wrap(data.getBytes());
        socketChannel.write(byteBuffer);
        System.in.read();
        socketChannel.close();
    }
}
