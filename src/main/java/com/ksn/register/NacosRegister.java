package com.ksn.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ksn.common.CommonConfig;
import com.ksn.common.GlobalConstants;
import com.ksn.common.URL;
import com.ksn.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 11:01
 * @description: nacos注册中心
 */
@Slf4j
public class NacosRegister implements IRegister {

    protected NamingService namingService;
    // protected String ip;
    // protected Integer port;
    protected String serverAddr;
    protected String namespace;

    public NacosRegister() {
        init();
    }

    @Override
    public void register(URL url) {
        try {
            namingService.registerInstance(url.getRegisterName(), createInstance(url));
            log.info("{}注册到{}:{}，注册成功", url.getRegisterName(), url.getRegisterAddr(), url.getRegisterPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregister(URL url) {
        try {
            namingService.deregisterInstance(url.getRegisterName(), createInstance(url));
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(URL url, NotifyListener listener) {
        try {
            namingService.subscribe(url.getRegisterName(), listener);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
        try {
            namingService.unsubscribe(url.getRegisterName(), listener);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Instance> lookup(URL url) {
        List<Instance> list = new ArrayList<>();
        try {
            list = namingService.getAllInstances(url.getRegisterName());
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Instance createInstance(URL url) {
        HashMap<String, String> map = new HashMap<>();
        String ip = url.getRegisterAddr();
        int port = url.getRegisterPort();
        map.put(GlobalConstants.REGISTER_TYPE, url.getType());
        map.put(GlobalConstants.APPLICATION_NAME, url.getApplicationName());
        map.put(GlobalConstants.INTERFACE_NAME, url.getInterfaceName());
        map.put(GlobalConstants.TIMESTAMP, url.getTimestamp().toString());
        map.put(GlobalConstants.REGISTER_ADDRESS, new StringBuilder(ip).append(':').append(port).toString());
        String s = new StringBuilder(url.getServiceAddr()).append(':').append(url.getPort()).toString();
        map.put(GlobalConstants.NETTY_ADDRESS, s);
        Instance instance = new Instance();
        instance.setIp(url.getServiceAddr());
        instance.setPort(url.getServicePort());
        instance.setMetadata(map);
        return instance;
    }

    private void init() {
        try {
            serverAddr = ApplicationContextUtil.getBean(CommonConfig.class).getRegistryAddress();
            namespace = ApplicationContextUtil.getBean(CommonConfig.class).getNamespace();
            Properties properties = new Properties();
            properties.setProperty("serverAddr", serverAddr);
            properties.setProperty("namespace", namespace);
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
}
