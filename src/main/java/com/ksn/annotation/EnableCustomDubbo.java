package com.ksn.annotation;

import com.ksn.config.consumer.CustomDubboRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2022/3/23 11:56
 * @description: 自定义enabledubbo注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomDubboRegistrar.class)
public @interface EnableCustomDubbo {

    String[] scanPackages() default {};
}
