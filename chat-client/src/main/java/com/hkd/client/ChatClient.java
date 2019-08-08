package com.hkd.client;

import com.google.protobuf.ByteString;
import com.hkd.client.handler.ChatClientHandler;
import com.hkd.client.handler.ChatFileHandler;
import com.hkd.common.codec.decoder.FileMessageDecoder;
import com.hkd.common.codec.encoder.FileMessageEncoder;
import com.hkd.common.proto.ChatFileProtocol;
import com.hkd.common.proto.ChatProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatClient {

    private static Logger logger = LoggerFactory.getLogger(ChatClient.class);
    public static String username = null;

    private static final int port = 9898;
    private static final String host = "localhost";
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.HOURS, new ArrayBlockingQueue(1), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("listen terminal input thread");
            return thread;
        }
    });

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            logger.error("username is null exit.");
            System.exit(1);
        }
        username = args[0];

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new IdleStateHandler(10, 10, 10))
                        .addLast(new ProtobufVarint32FrameDecoder())
                        .addLast(new ProtobufDecoder(ChatProtocol.ChatProto.getDefaultInstance()))
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        .addLast(new ProtobufEncoder())
                        .addLast(new ChatClientHandler());

            }
        }).channel(NioSocketChannel.class).remoteAddress(host, port);
        BufferedReader bufferedReader = null;
        Channel channel = null;
        try {
            ChannelFuture sync = bootstrap.connect().sync().addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("连接成功");
                }
            });
            channel = sync.channel();
            boolean flag = true;
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while (flag) {
                System.out.println("===========输入聊天信息=======to:username");
                String s = bufferedReader.readLine();
                if (s != null) {
                    if (s.contains("to:")) {
                        String to = s.substring(s.indexOf("to:") + "to:".length());
                        ChatProtocol.ChatProto chat = ChatProtocol.ChatProto.newBuilder().setType(ChatProtocol.ChatProto.MsType.CAHT).setChatMsg(ChatProtocol.ChatMsg.newBuilder().setFrom(username).setTo(to).setBody(s.substring(0, s.indexOf("to"))).setTimestamp(LocalDateTime.now().toString()).build()).build();
                        channel.writeAndFlush(chat);
                    }else if(s.contains("file:")){
//                        String path = s.substring(s.indexOf("file:") + "file:".length());
                        FileChannel fileChannel=FileChannel.open(Paths.get("/Users/huangkangda/Documents/test.txt"), StandardOpenOption.READ,StandardOpenOption.WRITE);
                        ByteBuffer byteBuffer= ByteBuffer.allocate((int)fileChannel.size());
                        fileChannel.read(byteBuffer);
                        byte[] array = byteBuffer.array();
                        ChatProtocol.ChatProto zhangsan = ChatProtocol.ChatProto.newBuilder().setType(ChatProtocol.ChatProto.MsType.FILE).setFileMsg(ChatProtocol.FileMsg.newBuilder().setFrom(username).setTo("zhangsan").setFilename("test.txt").setBody(ByteString.copyFrom(array)).build()).build();
                        channel.writeAndFlush(zhangsan);
                    } else if (s.contains("quit")) {
                        flag = false;
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("连接线程中断！", e);
            throw e;
        } catch (IOException e) {
            logger.error("read input error!", e);
        } finally {
            group.shutdownGracefully();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
            if (channel != null) {
                channel.close();
            }

        }
    }
}
