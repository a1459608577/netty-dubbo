package com.ksn.manager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/25 11:19
 * @description: 管理future对象
 */
public class FutureManager {

    private static volatile FutureManager manager = null;

    private ConcurrentHashMap<String, CompletableFuture<Object>> PROMISES = new ConcurrentHashMap<>();

    private FutureManager() {}

    /**
     * 双重检测 + volatile + synchronized 线程安全，但是最优解是使用 枚举
     * @return com.ksn.manager.PromiseManager
     */
    public static FutureManager getInstance() {
        if (manager == null) {
            synchronized (FutureManager.class) {
                if (manager == null) {
                    manager = new FutureManager();
                }
            }
        }
        return manager;
    }

    public void putFuture(String id, CompletableFuture promise) {
        PROMISES.putIfAbsent(id, promise);
    }

    public CompletableFuture<Object> getFuture(String id) {
        return PROMISES.get(id);
    }
}
