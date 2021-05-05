package com.zo.middleware.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author zero-one.lu
 * @since 2021-05-05
 */
@Slf4j
public class KafkaProduceApi {


    public static void main(String[] args) {


        Map<String, Object> config = new HashMap<>();

        //配置ack=all机制
        config.put(ProducerConfig.ACKS_CONFIG,"all");
        //配置key value为stringserialzer
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
        //配置链接服务地址
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.91.128:9092,192.168.91.128:9093,192.168.91.128:9094");
        config.put(ProducerConfig.RETRIES_CONFIG,3);

        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<>(config);

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test-topic","test","hello world 000");

//
//        Future<RecordMetadata> send = kafkaProducer.send(producerRecord);
//        try {
//            RecordMetadata recordMetadata = send.get();
//            log.info("offeset {}",recordMetadata.offset());
//            log.info("partition {}",recordMetadata.partition());
//            log.info("partition {}",recordMetadata.topic());
//
//        } catch (Exception e) {
//           log.error("send message error",e);
//        }
        CountDownLatch latch = new CountDownLatch(1);
        kafkaProducer.send(producerRecord,(metadata,exception) -> {

            if (exception != null){
                log.error("send error ",exception);
                latch.countDown();
                return;
            }
            log.info("send success");
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("error",e);
        }

        kafkaProducer.close();
    }
}
