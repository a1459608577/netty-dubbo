package com.ksn.annotation;

import java.lang.annotation.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/21 10:20
 * @description: 自定义spi注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomSPI {

    String value();
}
