package com.hkd.client.handler;


import com.hkd.client.ChatClient;
import com.hkd.common.proto.ChatProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ChatClientHandler extends SimpleChannelInboundHandler<ChatProtocol.ChatProto> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChatProtocol.ChatProto zhangsan = ChatProtocol.ChatProto.newBuilder().setType(ChatProtocol.ChatProto.MsType.LOGIN).setLoginMsg(ChatProtocol.LoginMsg.newBuilder().setUsername(ChatClient.username).build()).build();
        ctx.writeAndFlush(zhangsan);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatProtocol.ChatProto msg) throws IOException {
        if(msg.getType()== ChatProtocol.ChatProto.MsType.CAHT){
            System.out.println("from "+msg.getChatMsg().getFrom()+":"+msg.getChatMsg().getBody());
        }else if(msg.getType()== ChatProtocol.ChatProto.MsType.FILE){
            System.out.println("from "+msg.getFileMsg().getFrom()+":"+msg.getFileMsg().getFilename());
            Path path = Paths.get("/Users/huangkangda/Documents/", ChatClient.username);
            if(!path.toFile().exists()){
                Files.createDirectories(path);
            }
            try(FileChannel open = FileChannel.open(Paths.get(path.toAbsolutePath().toString(),msg.getFileMsg().getFilename()), StandardOpenOption.CREATE,StandardOpenOption.WRITE)){
                open.write(ByteBuffer.wrap(msg.getFileMsg().getBody().toByteArray()));
            } catch (IOException e) {
            }
        }
    }
}
