package com.ksn.manager;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 17:02
 * @description: 管理通道
 */
@Slf4j
public class ChannelManager {

    private static ConcurrentHashMap<String, Channel> map = new ConcurrentHashMap<>();

    private static volatile ChannelManager manager = null;

    private ChannelManager() {}

    /**
     * 双重检测 + volatile + synchronized 线程安全，但是最优解是使用 枚举
     * @return com.ksn.manager.ChannelManager
     */
    public static ChannelManager getInstance() {
        if (manager == null) {
            synchronized (ChannelManager.class) {
                if (manager == null) {
                    manager = new ChannelManager();
                }
            }
        }
        return manager;
    }

    public Channel getChannel(String key) {
        return map.get(key);
    }

    public Map<String, Channel> getMap() {
        return map;
    }

    public Channel remove(String key) {
        return map.remove(key);
    }

    public void put(String key, Channel channel) {
        if (channel == null) {
            log.warn("Channel can not empty");
            return;
        }
        map.put(key, channel);
    }
}
