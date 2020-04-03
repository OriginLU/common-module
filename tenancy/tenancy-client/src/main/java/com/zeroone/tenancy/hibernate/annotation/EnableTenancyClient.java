package com.zeroone.tenancy.hibernate.annotation;

import com.zeroone.tenancy.hibernate.autoconfigure.HibernateTenancyAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HibernateTenancyAutoConfiguration.class)
public @interface EnableTenancyClient {

}
