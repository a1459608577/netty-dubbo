package com.ksn.register;

import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.ksn.manager.InstanceManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 10:58
 * @description: 订阅事件监听器
 */
@Slf4j
public class NotifyListener implements EventListener {

    @Override
    public void onEvent(Event event) {
        NamingEvent namingEvent = (NamingEvent) event;
        String name = namingEvent.getServiceName();
        log.info("监听到注册中心{}的事件, instances的长度为： {}", name, namingEvent.getInstances().size());
        if (InstanceManager.INSTANCE.getInstance(name) != null) {
            InstanceManager.INSTANCE.remove(name);
        }
        InstanceManager.INSTANCE.setInstance(name, namingEvent.getInstances());
    }

}
