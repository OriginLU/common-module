package com.zeroone.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解,启用api日志
 * <p>
 * 日志内容参见:
 * <p>
 * {@link com.tqlog.common.aspect.RestApiLog}
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestApiLog {

}
