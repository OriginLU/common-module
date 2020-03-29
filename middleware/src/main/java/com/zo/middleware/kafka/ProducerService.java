package com.zo.middleware.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Charles
 * @since 2020-03-29
 */
@Service
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${tenant.topic}")
    private String topic;


    //发送消息方法
    public void send() {

        kafkaTemplate.send(topic, String.valueOf(System.currentTimeMillis()));
    }
}
