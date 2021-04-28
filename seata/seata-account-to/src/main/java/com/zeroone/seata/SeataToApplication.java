package com.zeroone.seata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EntityScan("com.zeroone.seata.entity")
@EnableDiscoveryClient
public class SeataToApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataToApplication.class,args);
    }
}
