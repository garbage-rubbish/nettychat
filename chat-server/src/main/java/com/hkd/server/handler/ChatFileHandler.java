package com.hkd.server.handler;

import com.hkd.common.proto.ChatFileProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ChatFileHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ChatFileProtocol){
            ChatFileProtocol chatFileProtocol=(ChatFileProtocol)msg;
            String to = chatFileProtocol.getTo();
            ChatHandler.SESSION.get(to).writeAndFlush(chatFileProtocol);
        }else{
            ReferenceCountUtil.retain(msg);
            ctx.fireChannelRead(msg);
        }
    }
}
