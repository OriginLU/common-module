package com.charleslu.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author charles.lu
 */
@Configuration
@ConditionalOnProperty(prefix = "custom.swagger.configure",name = "enable",havingValue = "true")
public class SwaggerConfiguration {


    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${custom.swagger.scan.package:}")
    private String basePackage;

    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName + "Restful API")
                .description(applicationName)
                .version("1.0").build();
    }
}
