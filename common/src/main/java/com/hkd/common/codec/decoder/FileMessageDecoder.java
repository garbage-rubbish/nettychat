package com.hkd.common.codec.decoder;

import com.hkd.common.proto.ChatFileProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FileMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        if(i!=2653){
            ctx.fireChannelRead(in);
        }
        int reqid = in.readInt();
//        if(reqid!=1){
//            in.resetReaderIndex();
//            ctx.fireChannelRead(in);
//        }else {
            int fromLength = in.readInt();
            ByteBuf fromByteBuf = in.readBytes(fromLength);
            String from=fromByteBuf.toString();
            int toLength = in.readInt();
            String to = in.readBytes(toLength).toString();
            int filenameLength = in.readInt();
            String filename = in.readBytes(filenameLength).toString();
            int bodyLength = in.readInt();
            ByteBuf byteBuf = in.readBytes(bodyLength);
            byte[] bytes=new byte[bodyLength];
            byteBuf.getBytes(0,bytes);
            ChatFileProtocol chatFileProtocol=new ChatFileProtocol();
            chatFileProtocol.setBody(bytes);
            chatFileProtocol.setFrom(from);
            chatFileProtocol.setTo(to);
            chatFileProtocol.setReqId(reqid);
            chatFileProtocol.setFilename(filename);
            out.add(chatFileProtocol);
        //}



    }
}
