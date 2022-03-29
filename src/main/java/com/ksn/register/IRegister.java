package com.ksn.register;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ksn.annotation.CustomSPI;
import com.ksn.common.URL;

import java.util.List;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 10:15
 * @description: 注册中心接口
 */
@CustomSPI("nacos")
public interface IRegister {

    /**
     * 注册
     * @param url
     */
    void register(URL url);
    /**
     * 取消注册
     * @param url
     */
    void unregister(URL url);
    /**
     * 订阅
     * @param url
     * @param listener
     */
    void subscribe(URL url, NotifyListener listener);
    /**
     * 取消订阅
     * @param url
     * @param listener
     */
    void unsubscribe(URL url, NotifyListener listener);
    /**
     * 获取全部实例
     * @param url
     * @return java.util.List<com.ksn.common.URL>
     */
    List<Instance> lookup(URL url);
}
