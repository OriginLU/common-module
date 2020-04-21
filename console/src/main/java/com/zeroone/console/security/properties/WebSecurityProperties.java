package com.zeroone.console.security.properties;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(prefix = "custom.security")
public class WebSecurityProperties {


    private String ignoreUrls;
}
