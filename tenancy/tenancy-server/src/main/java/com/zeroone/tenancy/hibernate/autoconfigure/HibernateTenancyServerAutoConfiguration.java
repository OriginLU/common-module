package com.zeroone.tenancy.hibernate.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Charles
 * @since 2020-04-03
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties({LiquibaseProperties.class})
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate",name = {"tenant_identifier_resolver", "multi_tenant_connection_provider"})
public class HibernateTenancyServerAutoConfiguration {

}
