package com.ksn.rpc;

import com.ksn.annotation.CustomSPI;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 12:19
 * @description:
 */
@CustomSPI("netty")
public interface IProviderServer {

    /**
     * 启动服务
     */
    void start(String ip, Integer port);

    /**
     * 关闭服务
     */
    void stop();
}
