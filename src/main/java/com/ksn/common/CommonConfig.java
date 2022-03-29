package com.ksn.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 17:51
 * @description: 公共配置
 */
@ConfigurationProperties(prefix = "netty-dubbo.config")
public class CommonConfig {

    private String rpcType;

    private String registryType;

    private String registryAddress;

    private String namespace;

    private String applicationName;
    /**
     * netty启动端口
     **/
    private Integer port;

    @Autowired
    public Environment env;

    @PostConstruct
    private void init() {
        if (StringUtils.isEmpty(this.getRpcType())) {
            String rpcType = env.resolvePlaceholders("${netty-dubbo.config.rpc-type:}");
            if (StringUtils.isEmpty(rpcType)) {
                rpcType = env.resolvePlaceholders("${netty-dubbo.config.rpc-type:netty}");
            }
            this.setRpcType(rpcType);
        }
        if (StringUtils.isEmpty(this.getNamespace())) {
            String namespace = env.resolvePlaceholders("${netty-dubbo.config.namespace:}");
            if (StringUtils.isEmpty(namespace)) {
                namespace = env.resolvePlaceholders("${netty-dubbo.config.namespace:public}");
            }
            this.setNamespace(namespace);
        }
        if (StringUtils.isEmpty(this.getRegistryType())) {
            String registryType = env.resolvePlaceholders("${netty-dubbo.config.registry-type:}");
            if (StringUtils.isEmpty(registryType)) {
                registryType = env.resolvePlaceholders("${netty-dubbo.config.registry-type:nacos}");
            }
            this.setRegistryType(registryType);
        }
        if (StringUtils.isEmpty(this.getRegistryAddress())) {
            String registryAddress = env.resolvePlaceholders("${netty-dubbo.config.registry-address:}");
            if (StringUtils.isEmpty(registryAddress)) {
                registryAddress = env.resolvePlaceholders("${netty-dubbo.config.registry-address:127.0.0.1:8848}");
            }
            this.setRegistryAddress(registryAddress);
        }
        if (StringUtils.isEmpty(this.getApplicationName())) {
            String name = env.resolvePlaceholders("${netty-dubbo.config.application-name:}");
            if (StringUtils.isEmpty(name)) {
                name = env.resolvePlaceholders("${spring.application.name:}");
            }
            this.setApplicationName(name);
        }
        if (this.getPort() == null) {
            String port = env.resolvePlaceholders("${netty-dubbo.config.port:}");
            this.setPort(port == null || port.length() == 0 ? GlobalConstants.DEFAULT_PORT : Integer.parseInt(port));
        }
    }

    public String getRpcType() {
        return rpcType;
    }

    public void setRpcType(String rpcType) {
        this.rpcType = rpcType;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String nacosAddress) {
        this.registryAddress = nacosAddress;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
