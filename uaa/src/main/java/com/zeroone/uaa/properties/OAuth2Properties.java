package com.zeroone.uaa.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2",ignoreInvalidFields = true)
public class OAuth2Properties {


   private String mode;
}
