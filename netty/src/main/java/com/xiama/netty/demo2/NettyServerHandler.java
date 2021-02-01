package com.xiama.netty.demo2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Xiaoma
 * @Date: 2021/2/1 0001 21:57
 * @Email: 1468835254@qq.com
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 自定义taskQueue与scheduleTaskQueue,当业务耗时过长时,异步处理
     * ChannelHandlerContext上下文对象
     * Object 客户端发送的数据
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        // 会提交到 taskQueue 中执行
        ctx.channel().eventLoop().execute(new Runnable() {
            @SneakyThrows
            public void run() {
                TimeUnit.SECONDS.sleep(7);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Server received message", CharsetUtil.UTF_8));
            }
        });
        // 会提交到 scheduleTaskQueue 中定时执行
        ctx.channel().eventLoop().schedule(new Runnable() {
            @SneakyThrows
            public void run() {
                TimeUnit.SECONDS.sleep(7);
                ctx.writeAndFlush(Unpooled.copiedBuffer("Server received message", CharsetUtil.UTF_8));
            }
        }, 7, TimeUnit.SECONDS);
    }

    /**
     * 数据读取完毕
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Server received message", CharsetUtil.UTF_8));
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
