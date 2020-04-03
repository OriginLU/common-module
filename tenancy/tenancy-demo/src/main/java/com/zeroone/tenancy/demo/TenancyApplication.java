package com.zeroone.tenancy.demo;

import com.zeroone.tenancy.hibernate.annotation.EnableTenancyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableTenancyClient
@EnableDiscoveryClient
@EnableEurekaClient
@EntityScan
public class TenancyApplication {


    public static void main(String[] args) {
        SpringApplication.run(TenancyApplication.class,args);
    }
}
