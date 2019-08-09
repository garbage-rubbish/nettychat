package com.hkd.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class ChatClient {
    private String hostname;
    private int port;
    private Channel channel;
    private int nThreads;
    private Bootstrap bootstrap;
    private EventLoopGroup eventExecutors;
    private HandlerInitializer channelHandler;


    public ChatClient(String hostname, int port,HandlerInitializer initializer) {
        this(hostname, port, 0,initializer);
    }

    public ChatClient(String hostname, int port, int nThreads,HandlerInitializer channelHandler) {
        this.hostname = hostname;
        this.port = port;
        this.nThreads = nThreads;
        this.channelHandler=channelHandler;
        channelHandler.chatClient=this;
        init();
    }

    private void init() {
        bootstrap = new Bootstrap();
        eventExecutors = new NioEventLoopGroup(nThreads);
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(channelHandler);
    }

    public Channel connect(ChannelFutureListener reConnectListener) throws InterruptedException {
        if(channel !=null && channel.isActive()){
            return channel;
        }
        try{
            ChannelFuture chan = bootstrap.connect(new InetSocketAddress(hostname, port)).sync();
            chan.addListener(reConnectListener);
            this.channel=chan.channel();
        }catch (Exception e){
            if(e instanceof IOException){
                Thread.sleep(3000);
                log.warn("connect exception try reconnect");
                connect(reConnectListener);
            }
        }
        return channel;
    }

    public void sendData(Object o){
        channel.writeAndFlush(o);
    }


    public void disConnect(){
        channel.disconnect();
    }

    public void close(ChannelFutureListener listener){
        ChannelFuture close = channel.close();
        eventExecutors.shutdownGracefully();
        close.addListener(listener);
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}
