package com.ksn.rpc.client;

import com.ksn.manager.ChannelManager;
import com.ksn.manager.FutureManager;
import com.ksn.rpc.IConsumerClient;
import com.ksn.rpc.RequestDataInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 16:45
 * @description: netty消费者 客户端
 */
@Slf4j
public class NettyConsumerClient implements IConsumerClient {

    private String host;
    private Integer port;
    private static Bootstrap bootstrap;

    @Override
    public void init() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast("encoder", new ObjectEncoder());
                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new IdleStateHandler(0,  4, 0));
                        pipeline.addLast(new NettyConsumerClientHandler(host, port));
                    }
                });
    }

    @Override
    public Channel connect(String ip, Integer port) {
        if (bootstrap == null) {
            log.warn("please initialize...");
            return null;
        }
        this.host = ip;
        this.port = port;
        String key = new StringBuilder(ip).append(':').append(port).toString();
        ChannelManager manager = ChannelManager.getInstance();
        Channel channel = manager.getChannel(key);
        if (channel == null) {
            try {
                channel = bootstrap.connect(ip, port).sync().channel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            manager.put(key, channel);
        }
        return channel;
    }

    @Override
    public Channel connect() {
        if (host == null || port == null) {
            throw new RuntimeException("host or port is null");
        }
        return connect(host, port);
    }

    @Override
    public CompletableFuture sendMsg(RequestDataInfo msg, Channel channel) {
        CompletableFuture future = new CompletableFuture();
        channel.writeAndFlush(msg).addListener(item -> {
            if (item.isSuccess()) {
                log.debug("message send success...");
            }
        });
        FutureManager.getInstance().putFuture(msg.getRequestId(), future);
        return future;
    }

}
