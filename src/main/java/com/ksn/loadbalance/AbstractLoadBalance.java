package com.ksn.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ksn.common.GlobalConstants;

import java.util.List;
import java.util.Map;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/28 16:26
 * @description:
 */
public abstract class AbstractLoadBalance implements ILoadBalance {

    @Override
    public String select(List<Instance> list) {
        if (list == null || list.size() == 0) {
            throw new RuntimeException("list不能为空");
        }
        Instance instance = list.size() > 1 ? doSelect(list) : list.get(0);
        Map<String, String> map = instance.getMetadata();
        return map.get(GlobalConstants.NETTY_ADDRESS);
    }

    protected abstract Instance doSelect(List<Instance> list);
}
