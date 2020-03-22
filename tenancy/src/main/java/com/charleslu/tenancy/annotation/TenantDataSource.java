package com.charleslu.tenancy.annotation;

import java.lang.annotation.*;

/**
 * 动态数据源注解
 *
 * @Author tanglh
 * @Date 2018/11/29 09:41
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantDataSource {
    /**
     * 租户名称
     * 默认为空表示所有数据源都会遍历访问
     *
     * @return
     */
    String[] value() default {};

    /**
     * 默认异步执行
     *
     * @return
     */
    boolean async() default true;
}