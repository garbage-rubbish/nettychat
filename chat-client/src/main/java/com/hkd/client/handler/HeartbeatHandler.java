package com.hkd.client.handler;

import com.hkd.common.proto.ChatProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        //client write idle send hb packet
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                ChatProtocol.ChatProto heartbeat = ChatProtocol.ChatProto.newBuilder().setType(ChatProtocol.ChatProto.MsType.HEARTBEATA).setHbMsg(ChatProtocol.HeartbeatMsg.newBuilder().setReqId(UUID.randomUUID().toString().replaceAll("-", "")).build()).build();
                ctx.writeAndFlush(heartbeat);
            }
            //
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                log.warn("read idle close channel...");
                ctx.channel().close();
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ChatProtocol.ChatProto && ((ChatProtocol.ChatProto) msg).getType() == ChatProtocol.ChatProto.MsType.HEARTBEATA) {
            log.info("receive heartbeat {}", ((ChatProtocol.ChatProto) msg).getHbMsg().getReqId());
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
