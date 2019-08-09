package com.hkd.client;


import com.hkd.client.handler.ChatClient;
import com.hkd.client.handler.HandlerInitializer;
import com.hkd.client.handler.ReConnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static String hostname="localhost";
    private static int port=9898;

    public static void main(String[] args) {
        HandlerInitializer handlerInitializer = new HandlerInitializer();
        ChatClient chatClient = new ChatClient(hostname, port, handlerInitializer);
        try {
            chatClient.connect(new ReConnectListener(chatClient));
        } catch (InterruptedException e) {
            logger.warn("Interrupted!",e);
            Thread.currentThread().interrupt();
        }
    }
}
