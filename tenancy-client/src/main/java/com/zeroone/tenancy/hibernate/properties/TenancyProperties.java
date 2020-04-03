package com.zeroone.tenancy.hibernate.properties;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Accessors(chain = true)
@ToString
@Configuration
@ConfigurationProperties(prefix = "tenancy.client", ignoreInvalidFields = true)
public class TenancyProperties {


    /**
     * 数据源服务名称
     */
    private String tenantServerName;

    /**
     * 实例名称
     */
    private String instantName;

}
