package com.ksn.config.consumer;

import com.ksn.common.GlobalConstants;
import com.ksn.loadbalance.ILoadBalance;
import com.ksn.loadbalance.LoadBalanceFactory;
import com.ksn.manager.ChannelManager;
import com.ksn.manager.InstanceManager;
import com.ksn.rpc.IConsumerClient;
import com.ksn.rpc.RequestDataInfo;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 16:30
 * @description:
 */
public class CustomDubboInvocationHandler implements InvocationHandler {

    private String host;
    private Integer port;
    private String className;
    private IConsumerClient consumerClient;

    public CustomDubboInvocationHandler(String className, IConsumerClient consumerClient) {
        this.className=  className;
        this.consumerClient = consumerClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InterruptedException, ExecutionException {
        // 负载均衡选择提供者获取连接的ip跟端口
        ILoadBalance loadBalance = LoadBalanceFactory.getLoadBalance();
        String addr = loadBalance.select(InstanceManager.INSTANCE.getInstance(GlobalConstants.PROVIDERS_CATEGORY + ":" + className));
        String[] arr = addr.split(":");
        this.host = arr[0];
        this.port=  Integer.parseInt(arr[1]);
        // 获取channle，从缓存获取，获取不到再创建，key为ip'+端口
        String key = new StringBuilder(host).append(':').append(port).toString();
        Channel channel = ChannelManager.getInstance().getChannel(key);
        if (channel == null) {
            channel = consumerClient.connect(host, port);
        }
        // 构建消息对象
        String methodName = method.getName();
        Class<?>[] types = method.getParameterTypes();
        RequestDataInfo msg = new RequestDataInfo();
        msg.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        msg.setClassName(className);
        msg.setMethodName(methodName);
        msg.setParams(args);
        msg.setTypes(types);
        // channel发送消息
        CompletableFuture future = consumerClient.sendMsg(msg, channel);
        return future.get();
    }
}
