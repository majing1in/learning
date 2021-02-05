package com.xiama.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebServer {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 基于 HTTP 协议，使用 HTTP 编解码器
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 以块方式读写
                            pipeline.addLast(new ChunkedWriteHandler());
                            // HTTP 数据传输过程中是分段的，HttpObjectAggregator将多个段聚合
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                             * WebSocket 数据是以帧的形式传递
                             * ws://localhost:9999/xxx 请求uri
                             * WebSocketServerProtocolHandler 将 HTTP 协议升级为 ws 协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                            pipeline.addLast(new ServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(9999).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
