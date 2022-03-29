package com.ksn.config.consumer;

import com.ksn.rpc.IConsumerClient;
import com.ksn.spi.ExtensionLoader;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 16:21
 * @description: 自定义dubbo beanfactory类
 */
public class CustomDubboFactoryBean implements FactoryBean<Object> {

    private Class<?> type;
    private String className;

    @Override
    public Object getObject() {
        IConsumerClient consumerClient = ExtensionLoader.getExtensionLoader(IConsumerClient.class).getExtension("netty");
        // 初始化netty客户端
        consumerClient.init();
        CustomDubboInvocationHandler handler = new CustomDubboInvocationHandler(className, consumerClient);
        if (!type.isInterface()) {
            throw new RuntimeException(type.getName() + " is not an interface");
        }
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }


    public CustomDubboFactoryBean setType(Class<?> type) {
        this.type = type;
        return this;
    }

    public CustomDubboFactoryBean setClassName(String className) {
        this.className = className;
        return this;
    }
}
