package com.ksn.manager;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 15:20
 * @description: 管理nacos注册中心实例
 */
public enum InstanceManager {

    /**
     * 单例
     **/
    INSTANCE;

    private final ConcurrentHashMap<String, List<Instance>> map = new ConcurrentHashMap<>();

    public List<Instance> getInstance(String name) {
        return map.get(name);
    }

    public void setInstance(String name, List<Instance> list) {
        map.putIfAbsent(name, list);
    }

    public void remove(String name) {
        map.remove(name);
    }

    public ConcurrentHashMap<String, List<Instance>> getAllObject() {
        return map;
    }

}
