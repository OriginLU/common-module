package com.zeroone.tenancy.hibernate.annotation;

import com.zeroone.tenancy.hibernate.autoconfigure.HibernateTenancyServerAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Charles
 * @since 2020-04-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HibernateTenancyServerAutoConfiguration.class)
public @interface EnableTenancyServer {
}
