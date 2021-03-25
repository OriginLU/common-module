package com.zeroone.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author zero-one.lu
 * @since 2020-12-02
 */
@SpringBootApplication
//@EnableDiscoveryClient
//@EnableHystrix
//@EnableFeignClients
@EntityScan("com.zeroone.uaa.entity")
@EnableJpaRepositories
@EnableConfigurationProperties
public class UaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class,args);
    }
}
