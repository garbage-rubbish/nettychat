package com.hkd.server;

import com.hkd.server.init.ChatServerHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer {

    private Logger logger= LoggerFactory.getLogger(ChatServer.class);

    private final int port=9898;

    public static void main(String[] args) {
        new ChatServer().start();
    }


    private ChatServer(){

    }

    public void start(){
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(boss,worker).childHandler(new ChatServerHandlerInitializer());
        serverBootstrap.channel(NioServerSocketChannel.class);
        try {
            ChannelFuture sync = serverBootstrap.bind(port).sync();
            sync.addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()){
                    logger.info("绑定本地端口成功！");
                }else {
                    logger.error("绑定本地端口失败！");
                    future.channel().close();
                }
            });

            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
