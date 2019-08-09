package com.hkd.server.handler;

import com.hkd.common.proto.ChatProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatHandler extends SimpleChannelInboundHandler<ChatProtocol.ChatProto> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatProtocol.ChatProto msg) throws Exception {
        if(msg.getType()== ChatProtocol.ChatProto.MsType.HEARTBEATA){
            log.info("receive heartbeat {}",msg.getHbMsg().getReqId());
            ctx.writeAndFlush(msg);
        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
