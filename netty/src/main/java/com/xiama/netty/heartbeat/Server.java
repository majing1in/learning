package com.xiama.netty.heartbeat;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Server {

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
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new StringEncoder())
                                    .addLast("decoder", new StringDecoder())
                                    /*
                                        处理空闲状态的处理器
                                        readerIdleTime 多长时间没有读操作，会发送心跳包检测连接
                                        writerIdelTime 多长时间没有写操作，会发送心跳包检测连接
                                        allIdelTime 读写都没操作的时间，会发送心跳包检测连接
                                        触发后，会传递给管道的下一个handler处理，该方法处理userEventTiggered
                                     */
                                    .addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS))
                                    // 处理空闲连接，自定义handler
                                    .addLast(new ServerHandler());
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
