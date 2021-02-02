package com.xiama.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ServerHandlerByMyself extends SimpleChannelInboundHandler<HttpObject> {

    // 读取事件触发
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if (httpObject instanceof HttpRequest) {
            System.out.println("客户端地址:" + channelHandlerContext.channel().remoteAddress());
            // 资源过滤
            HttpRequest httpRequest = (HttpRequest) httpObject;
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println(" 请求图标过滤 ");
                return;
            }
            ByteBuf byteBuf = Unpooled.copiedBuffer("Everything is ok!", CharsetUtil.UTF_8);
            // 构建相应
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            // 返回响应
            channelHandlerContext.writeAndFlush(response);
        }
    }
}
