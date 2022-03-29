package com.ksn.rpc.server;

import com.ksn.manager.ChannelManager;
import com.ksn.manager.ProviderManager;
import com.ksn.rpc.RequestDataInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 14:36
 * @description: netty handler
 */
@Slf4j
public class NettyProviderServerHandler extends ChannelInboundHandlerAdapter {

    private String host;
    private Integer port;

    private AtomicInteger count = new AtomicInteger();

    public NettyProviderServerHandler() {}

    public NettyProviderServerHandler(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("客户端{}连接成功", channel.remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (msg instanceof RequestDataInfo) {
            log.info("服务端接受到来自{}的信息", channel.remoteAddress());
            RequestDataInfo info = (RequestDataInfo) msg;
            String className = info.getClassName();
            String methodName = info.getMethodName();
            Object[] params = info.getParams();
            Class<?>[] types = info.getTypes();

            Object provider = ProviderManager.INSTANCE.getProvider(className);
            Method method = provider.getClass().getMethod(methodName, types);
            Object result = method.invoke(provider, params);
            info.setResult(result);
            ctx.writeAndFlush(msg);
        }
        log.info("服务端接受心跳");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                if (count.getAndIncrement() == 3) {
                    log.info("服务器断开和客户端的连接");
                    ChannelManager.getInstance().remove(host + ":" + port);
                    ctx.close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("客户端{}断开连接", channel.remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出现异常：{}", cause.getMessage());
        ChannelManager.getInstance().remove(host + ":" + port);
        ctx.close();
    }
}
