package com.ksn.rpc;

import com.ksn.annotation.CustomSPI;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 16:47
 * @description: 消费者客户端接口
 */
@CustomSPI("netty")
public interface IConsumerClient {

    /**
     * 初始化客户端
     * @return io.netty.bootstrap.Bootstrap
     */
    void init();

    /**
     * 连接服务器
     * @param ip
     * @param port
     * @return io.netty.channel.Channel
     */
    Channel connect(String ip, Integer port) throws InterruptedException;

    /**
     * 连接服务器
     * @return io.netty.channel.Channel
     */
    Channel connect();

    /**
     * 发送消息
     * @param msg
     * @param channel
     */
    CompletableFuture sendMsg(RequestDataInfo msg, Channel channel) throws InterruptedException;
}
