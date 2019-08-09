package com.hkd.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InActiveHandler extends ChannelInboundHandlerAdapter {

    private ChatClient chatClient;
    InActiveHandler(ChatClient chatClient){
        this.chatClient=chatClient;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      log.warn("disconnect.. try reconnect");
        chatClient.connect(new ReConnectListener(chatClient));

    }
}
