package com.charleslu.tenancy.annotation;

import java.lang.annotation.*;

/**
 * 注解仅用于 TenantMissHandleInterface 实现类标识是否使用 MyHttpServletRequestWrapper 包装请求
 *
 * @author ziyan
 * @email zhengmengyan@charleslucar.com
 * @date 2018年12月29日
 * @see com.charleslu.tenancy.interceptor.TenantMissHandleInterface
 * @see com.charleslu.tenancy.interceptor.MyHttpServletRequestWrapper
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WrapperRequest {
}
