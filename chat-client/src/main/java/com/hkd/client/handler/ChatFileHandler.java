package com.hkd.client.handler;

import com.hkd.common.proto.ChatFileProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.checkerframework.checker.units.qual.C;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChatFileHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        if(msg instanceof ChatFileProtocol){
            ChatFileProtocol chatFileProtocol=(ChatFileProtocol)msg;
            String filename = chatFileProtocol.getFilename();
            FileChannel open = FileChannel.open(Paths.get("/Users/huangkangda/tmp/chat/file/", filename));
            ByteBuffer byteBuffer=ByteBuffer.allocate(chatFileProtocol.getBody().length);
            byteBuffer.put(chatFileProtocol.getBody());
            byteBuffer.flip();
            open.write(byteBuffer);
            open.close();
        }else{
            ReferenceCountUtil.retain(msg);
            ctx.fireChannelRead(msg);
        }
    }
}
