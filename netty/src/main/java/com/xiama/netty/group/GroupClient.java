package com.xiama.netty.group;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class GroupClient {

    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 7777;

    public void run() throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new StringEncoder())
                                    .addLast("decoder", new StringDecoder())
                                    .addLast(new GroupClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            Channel channel = future.channel();
            System.out.println(" Client init finish ");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                channel.writeAndFlush(message + "\r\n");
            }
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new GroupClient().run();
    }
}
