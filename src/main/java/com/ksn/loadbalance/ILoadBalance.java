package com.ksn.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ksn.annotation.CustomSPI;

import java.util.List;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/28 16:23
 * @description: 负载均衡接口
 */
@CustomSPI("random")
public interface ILoadBalance {

    /**
     * 负载均衡
     * @param list
     * @return java.lang.String
     */
    String select(List<Instance> list);
}
