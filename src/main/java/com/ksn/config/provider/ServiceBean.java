package com.ksn.config.provider;

import com.ksn.annotation.CustomService;
import com.ksn.common.CommonConfig;
import com.ksn.common.URL;
import com.ksn.common.URLBuilder;
import com.ksn.manager.ProviderManager;
import com.ksn.register.IRegister;
import com.ksn.rpc.IProviderServer;
import com.ksn.spi.ExtensionLoader;
import com.ksn.utils.ApplicationContextUtil;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 11:29
 * @description: 监听上下文刷新后进行加载和注册
 */
@Component
public class ServiceBean implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private CommonConfig config;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // 启动netty
        IProviderServer netty = ExtensionLoader.getExtensionLoader(IProviderServer.class).getExtension("netty");
        // 指定要扫描的包名
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.ksn"))
                .addScanners(new TypeAnnotationsScanner()));
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(CustomService.class);

        // 存到本地
        try {
            for (Class<?> clazz : classSet) {
                Object instance = clazz.newInstance();
                String name = clazz.getInterfaces()[0].getName();
                ProviderManager.INSTANCE.setProvider(name, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 构建URL
        Set<URL> urls = URLBuilder.build(config, classSet, false);
        // 注册到注册中心
        IRegister register = ExtensionLoader.getExtensionLoader(IRegister.class).getExtension("nacos");
        urls.stream().forEach(item -> {
            register.register(item);
        });
        netty.start(ApplicationContextUtil.getLocalIp(), config.getPort());
    }
}
