package com.xiama.netty.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 21:39
 * @Email: 1468835254@qq.com
 */
public class NettyServer {

    public static void main(String[] args) {
        // 创建 BossGroup,只处理连接请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建 workerGroup,真正的处理请求
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        // 创建服务器启动对象，配置启动参数
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            /*
             * 链式编程设置
             * 1、绑定两个线程
             * 2、NettyServer 作为服务器通道实现
             * 3、设置线程队列连接个数
             * 4、设置保持活动连接状态
             * 5、workerGroup 的 eventLoop设置处理器
             */
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 创建通道初始化对象，想pipeline中设置处理器
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println(" Server init finish! ");
            // 启动服务器，绑定端口，并且同步处理
            ChannelFuture future = serverBootstrap.bind(6668).sync();
            // 对关闭通道进行监听
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
