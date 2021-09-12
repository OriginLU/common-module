package com.zeroone.config;

import com.zeroone.ast.TenantQueryTranslatorFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateTenancyConfiguration {


    public final static String TENANT_FILTER = "TENANT_FILTER";

    public final static String MERCHANT_ID = "merchant_id";



    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return properties -> properties.put(AvailableSettings.QUERY_TRANSLATOR, TenantQueryTranslatorFactory.INSTANCE);
    }

}
