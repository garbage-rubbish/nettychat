package com.hkd.client.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReConnectListener implements ChannelFutureListener {

    private ChatClient chatClient;
    private ScheduledExecutorService threadPoolExecutor= Executors.newScheduledThreadPool(2);
    public ReConnectListener(ChatClient chatClient){
        this.chatClient=chatClient;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if(!future.isSuccess()){
           threadPoolExecutor.schedule(new Runnable() {
               @Override
               public void run() {
                   try {
                       chatClient.connect(ReConnectListener.this);
                   } catch (InterruptedException e) {
                       log.warn("Interrupted",e);
                       Thread.currentThread().interrupt();
                   }
               }
           },3,TimeUnit.SECONDS);
        }else {
            log.info("connected to {}:{}",chatClient.getHostname(),chatClient.getPort());
        }
    }
}
