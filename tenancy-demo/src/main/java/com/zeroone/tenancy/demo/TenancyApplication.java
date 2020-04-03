package com.zeroone.tenancy.demo;

import com.zeroone.tenancy.hibernate.annotation.EnableTenancy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableTenancy
@EnableDiscoveryClient
@EnableEurekaClient
@RibbonClient(name = "pay")
public class TenancyApplication {


    public static void main(String[] args) {
        SpringApplication.run(TenancyApplication.class,args);
    }
}
