package com.hkd.common.codec.encoder;

import com.google.protobuf.ByteString;
import com.hkd.common.proto.ChatFileProtocol;
import com.hkd.common.proto.ChatProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.codec.MessageToByteEncoder;

public class FileMessageEncoder extends MessageToByteEncoder<ChatFileProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ChatFileProtocol msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getReqId());
        out.writeInt(msg.getFrom().getBytes().length);
        out.writeBytes(msg.getFrom().getBytes());
        out.writeInt(msg.getTo().getBytes().length);
        out.writeBytes(msg.getTo().getBytes());
        out.writeInt(msg.getFilename().getBytes().length);
        out.writeBytes(msg.getFilename().getBytes());
        out.writeInt(msg.getBody().length);
        out.writeBytes(msg.getBody());
        int i = out.readableBytes();
    }
}
