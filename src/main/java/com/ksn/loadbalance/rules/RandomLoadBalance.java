package com.ksn.loadbalance.rules;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ksn.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/28 16:40
 * @description: 随机
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected Instance doSelect(List<Instance> list) {
        int size = list.size();
        int i = new Random(size).nextInt();
        return list.get(i);
    }
}
