package com.ksn.loadbalance.rules;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ksn.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/28 16:41
 * @description: 轮询
 */
public class PollLoadBalance extends AbstractLoadBalance {
    AtomicInteger count = new AtomicInteger();

    @Override
    protected Instance doSelect(List<Instance> list) {
        int size = list.size();
        return list.get(count.getAndIncrement() % size);
    }

}
