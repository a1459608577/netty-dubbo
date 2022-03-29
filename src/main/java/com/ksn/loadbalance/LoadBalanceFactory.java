package com.ksn.loadbalance;

import com.ksn.spi.ExtensionLoader;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/28 16:37
 * @description: 负载均衡工厂
 */
public class LoadBalanceFactory {

    public static ILoadBalance getLoadBalance() {
        ILoadBalance balance = ExtensionLoader.getExtensionLoader(ILoadBalance.class).getDefaultExtension();
        return balance;
    }
}
