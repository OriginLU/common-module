package com.zeroone.hystrix.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zero-one.lu
 * @since 2021-05-01
 */
@SpringBootApplication
@EnableFeignClients
@EnableHystrix
public class HystrixClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(HystrixClientApplication.class,args);
    }
}
