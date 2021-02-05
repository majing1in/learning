package com.xiama.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx 上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 事件转换
            IdleStateEvent event = (IdleStateEvent) evt;
            String tips = null;
            switch (event.state()) {
                case READER_IDLE:
                    tips = "读空闲";
                    break;
                case WRITER_IDLE:
                    tips = "写空闲";
                    break;
                case ALL_IDLE:
                    tips = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "==>" + tips);
        }
    }
}
