package com.ksn.rpc.client;

import com.ksn.manager.ChannelManager;
import com.ksn.manager.FutureManager;
import com.ksn.rpc.HeartBeatMessage;
import com.ksn.rpc.IConsumerClient;
import com.ksn.rpc.RequestDataInfo;
import com.ksn.spi.ExtensionLoader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 16:52
 * @description:
 */
@Slf4j
public class NettyConsumerClientHandler extends ChannelInboundHandlerAdapter {

    private String host;
    private Integer port;
    private AtomicInteger count = new AtomicInteger();

    public NettyConsumerClientHandler(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("The client connects to the server success...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("接受到服务端的信息-" + msg.toString());
        if (msg instanceof RequestDataInfo) {
            RequestDataInfo info = (RequestDataInfo) msg;
            CompletableFuture future = FutureManager.getInstance().getFuture(info.getRequestId());
            if (future != null) {
                future.complete(info.getResult());
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.WRITER_IDLE)) {
                log.info("客户端发送心跳");
                ctx.writeAndFlush(new HeartBeatMessage());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) {
        // 重连之前把对应的channel删除掉
        ChannelManager.getInstance().remove(this.host + ":" + this.port);
        ctx.channel().eventLoop().schedule(() -> {
            log.info("开始重连");
            IConsumerClient consumerClient = ExtensionLoader.getExtensionLoader(IConsumerClient.class).getExtension("netty");
            consumerClient.connect();
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client error：{}", cause.getMessage());
        ChannelManager.getInstance().remove(host + ":" + port);
        ctx.close();
    }
}
