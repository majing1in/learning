package com.xiama.netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 21:57
 * @Email: 1468835254@qq.com
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取实际数据
     * ChannelHandlerContext上下文对象
     * Object 客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server ctx = " + ctx);
        // ByteBuf 由 netty 提供
        ByteBuf byteBuffer = (ByteBuf) msg;
        System.out.println("message ==> " + byteBuffer.toString(CharsetUtil.UTF_8));
        System.out.println("client address ==> " + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Server received message", CharsetUtil.UTF_8));
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
