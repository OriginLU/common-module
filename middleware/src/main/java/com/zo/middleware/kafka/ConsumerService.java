package com.zo.middleware.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Charles
 * @since 2020-03-29
 */
@Slf4j
@Service
public class ConsumerService {



    @KafkaListener(topics = {"${tenant.topic}"},groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<?, String> record){

        Optional.ofNullable(record.value()).ifPresent(e -> log.info("receiver message：{}",e));

    }

}
