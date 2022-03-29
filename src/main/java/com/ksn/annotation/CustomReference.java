package com.ksn.annotation;

import java.lang.annotation.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 11:38
 * @description: 自定义Reference注解
 */
// @Autowired 在自定义注解里加这个也可以实现自动注入功能
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomReference {
}
