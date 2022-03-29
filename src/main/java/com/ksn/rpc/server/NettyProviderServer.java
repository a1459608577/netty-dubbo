package com.ksn.rpc.server;

import com.ksn.rpc.IProviderServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 12:18
 * @description: netty服务端
 */
@Slf4j
public class NettyProviderServer implements IProviderServer {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    @Override
    public void start(String host, Integer port) {
        try {
            // 启动netty服务端
            openServer(host, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void openServer(String host, Integer port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ChannelFuture channelFuture = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast("encoder", new ObjectEncoder());
                        pipeline.addLast(new IdleStateHandler(5, 0, 0));
                        pipeline.addLast(new NettyProviderServerHandler(host, port));
                    }
                }).bind(port);
        log.info("netty server is started...");
    }

}
