package com.ksn.annotation;

import java.lang.annotation.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/22 16:00
 * @description: 自定义服务提供者注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomService {
}
