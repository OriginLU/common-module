package com.zo.middleware.web;

import com.zo.middleware.kafka.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Charles
 * @since 2020-03-29
 */
@RestController
public class KafkaRest {

    @Autowired
    private ProducerService producerService;


    @GetMapping("send")
    public ResponseEntity<String> send(){

        producerService.send();
        return ResponseEntity.ok("success");
    }
}
