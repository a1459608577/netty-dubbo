package com.ksn.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 11:53
 * @description: spring上下文工具类
 */
@Slf4j
@Component
public class ApplicationContextUtil implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        ApplicationContextUtil.environment = environment;
    }

    public static <T>T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static String getLocalIp() {
        InetAddress localHost = null;
        try {
            localHost = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        return localHost.getHostAddress();
    }

    public static Integer getPort() {
        String port = environment.getProperty("server.port");
        return StringUtils.isEmpty(port) ? 8080 : Integer.parseInt(port);
    }
}
