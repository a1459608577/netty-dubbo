package com.ksn.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 10:21
 * @description: 注册中心信息
 */
@Data
public class URL implements Serializable {

    /**
     * 注册中心地址
     **/
    private String registerAddr;

    /**
     * 注册中心端口
     **/
    private Integer registerPort;

    /**
     * 注册上注册中心名字
     **/
    private String registerName;
    /**
     * 应用名字
     **/
    private String applicationName;

    /**
     * 接口名
     **/
    private String interfaceName;
    /**
     * 服务地址
     **/
    private String serviceAddr;
    /**
     * 服务端口
     **/
    private Integer servicePort;
    /**
     * 类型
     **/
    private String type;
    /**
     * netty端口
     **/
    private Integer port;

    /**
     * 时间戳
     **/
    private Long timestamp;

    /**
     * 其他参数，如方法名等
     **/
    private Map<String, String> params;

}
