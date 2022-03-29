package com.ksn.spi;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 10:41
 * @description: 用于保存值的Helper类。
 */
public class Holder<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
