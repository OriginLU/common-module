package com.zeroone.tcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EntityScan("com.zeroone.tcc.entity")
@EnableDiscoveryClient
@EnableJpaAuditing
public class ToApplication {


    public static void main(String[] args) {
        SpringApplication.run(ToApplication.class,args);
    }
}
