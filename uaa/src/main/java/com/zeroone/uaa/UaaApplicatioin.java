package com.zeroone.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author zero-one.lu
 * @since 2020-12-02
 */
@SpringBootApplication
//@EnableDiscoveryClient
//@EnableHystrix
//@EnableFeignClients
@EnableConfigurationProperties
public class UaaApplicatioin {

    public static void main(String[] args) {
        SpringApplication.run(UaaApplicatioin.class,args);
    }
}
