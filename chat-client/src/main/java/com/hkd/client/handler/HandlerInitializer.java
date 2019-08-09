package com.hkd.client.handler;

import com.google.common.collect.Lists;
import com.hkd.common.proto.ChatProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.Socket;
import java.util.List;

public class HandlerInitializer extends ChannelInitializer<SocketChannel> {
    private List<ChannelHandler> handlerList = Lists.newArrayList();
    protected ChatClient chatClient;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline().addLast(new IdleStateHandler(10, 8, 10))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(ChatProtocol.ChatProto.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new HeartbeatHandler())
                .addLast(new InActiveHandler(chatClient));
                //.addLast(new ChatClientHandler());
    }

    public void addHandler(ChannelHandler channelHandler) {
        handlerList.add(channelHandler);
    }
}
