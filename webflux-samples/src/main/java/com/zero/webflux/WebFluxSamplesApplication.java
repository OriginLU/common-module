package com.zero.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.zeroone.uaa.entity")
public class WebFluxSamplesApplication {


    public static void main(String[] args) {

        SpringApplication.run(WebFluxSamplesApplication.class,args);

    }
}
