package com.zeroone.seata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.zeroone.seata.entity")
public class SeataFromApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataFromApplication.class,args);
    }
}
