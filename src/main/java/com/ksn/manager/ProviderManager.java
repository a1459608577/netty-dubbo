package com.ksn.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 15:20
 * @description: 管理服务提供者
 */
public enum ProviderManager {

    /**
     * 单例
     **/
    INSTANCE;

    private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public Object getProvider(String name) {
        return map.get(name);
    }

    public void setProvider(String name, Object obj) {
        map.putIfAbsent(name, obj);
    }

    public Map<String, Object> getAllObject() {
        return map;
    }

}
