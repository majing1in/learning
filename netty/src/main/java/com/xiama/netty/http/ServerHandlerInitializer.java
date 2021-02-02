package com.xiama.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 向管道加入处理器
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 加入 netty 提供的编解码器
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        // 加入自定义解码器
        pipeline.addLast("MySelfServerHandler", new ServerHandlerByMyself());
    }
}
