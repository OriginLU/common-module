package com.charleslu.tenancy.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author tanglh
 * @Date 2018/12/04 12:06
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource.local", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class LocalDataSourceProperties {


    private Map<String, String> properties = new HashMap<>();

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
