package com.xiama.netty.group;

import com.sun.corba.se.impl.orbutil.ObjectUtility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.ObjectUtil;

import java.text.SimpleDateFormat;

public class GroupServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义 channel 组
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel channel = channelHandlerContext.channel();
        // 遍历 channels
        channels.forEach(c -> {
            if (c != channel) {
                c.write(channel.remoteAddress() + ":" + s + "\n");
            } else {
                c.write(" 自己 :" + s + "\n");
            }
        });
    }

    // 建立连接，首先执行
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 上线时推送给所有客户端
        channels.writeAndFlush("Client " + channel.remoteAddress() + "上线了" + "\n");
        channels.add(channel);
    }

    //  表示 channel 处于活动状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" Client linked success " + ctx.channel().remoteAddress() + "\n");
    }

    // 表示 channel 不活动状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" Client go out " + ctx.channel().remoteAddress() + "\n");
    }

    // 断开连接
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush(" Client go out " + channel.remoteAddress() + "\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
