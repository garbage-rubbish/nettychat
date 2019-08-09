package com.hkd.server.init;

import com.hkd.common.proto.ChatProtocol;
import com.hkd.server.handler.ChatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;


public class ChatServerHandlerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(10,10,10));
        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast(new ProtobufDecoder(ChatProtocol.ChatProto.getDefaultInstance()));
        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast(new ProtobufEncoder());
        ch.pipeline().addLast(new ChatHandler());

    }
}
