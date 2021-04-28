package com.zeroone.seata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EntityScan("com.zeroone.seata.entity")
@EnableFeignClients
@EnableDiscoveryClient
public class SeataFromApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataFromApplication.class,args);
    }
}
