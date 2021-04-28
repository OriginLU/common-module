package com.zeroone.tcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EntityScan("com.zeroone.tcc.entity")
@EnableDiscoveryClient
@EnableHystrix
@EnableFeignClients("com.zeroone.tcc.proxy")
public class FromApplication {

    public static void main(String[] args) {
        SpringApplication.run(FromApplication.class,args);
    }
}
