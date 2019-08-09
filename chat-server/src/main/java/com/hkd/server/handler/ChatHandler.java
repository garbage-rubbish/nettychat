package com.hkd.server.handler;

import com.hkd.common.proto.ChatProtocol;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ChannelHandler.Sharable
public class ChatHandler extends ChannelInboundHandlerAdapter {

    private Logger logger= LoggerFactory.getLogger(ChatHandler.class);

    public static final ConcurrentMap<String, Channel> SESSION=new ConcurrentHashMap<>(8);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ChatProtocol.ChatProto chatProto=null;
        if(msg instanceof ChatProtocol.ChatProto){
            chatProto=(ChatProtocol.ChatProto)msg;
            if(chatProto.getType()== ChatProtocol.ChatProto.MsType.CAHT){
                logger.info("receive {},{}",chatProto.getChatMsg().getFrom(),chatProto.getChatMsg().getBody());
                Channel channel = SESSION.get(chatProto.getChatMsg().getTo());
                channel.writeAndFlush(chatProto);
            }else if(chatProto.getType()== ChatProtocol.ChatProto.MsType.LOGIN){
                SESSION.put(chatProto.getLoginMsg().getUsername(),ctx.channel());
                logger.info("{}:登陆上线了",chatProto.getLoginMsg().getUsername());
            }else if(chatProto.getType()== ChatProtocol.ChatProto.MsType.FILE){
//                FileChannel.open(Paths.get("/Users/huangkangda/Documents/testCopy.txt"))
                Channel channel = SESSION.get(chatProto.getFileMsg().getTo());
                channel.writeAndFlush(chatProto);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("except：{}",cause);
    }
}
