package com.zeroone.uaa.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2",ignoreInvalidFields = true)
public class OAuth2Properties {


   /**
    * 授权集成模式[jwt,jdbc,in-memory]
    */
   private String authorizationMode;

   /**
    * 认证集成模式[jdbc,in-memory]
    */
   private String authenticateMode;
}
