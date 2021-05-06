package com.zeroone.rocketmq.api;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RocketMQApi {


    public final static Logger log = LoggerFactory.getLogger(RocketMQApi.class);

    public static void main(String[] args) {
//        send();
        sendAsync();
    }


    public static void send(){

        DefaultMQProducer producer = new DefaultMQProducer("test-group");

        producer.setNamesrvAddr("10.0.25.169:9876");
        Message message = new Message();
        message.setBody("test".getBytes(StandardCharsets.UTF_8));
        message.setTopic("test-topic");
        message.setTags("t-tags");

        try {
            producer.start();
            SendResult send = producer.send(message);
            log.info("send result：{}",send);
        } catch (Exception e) {
            log.error("send error",e);
        }
        producer.shutdown();
    }


    public static void sendAsync(){

        DefaultMQProducer producer = new DefaultMQProducer("test-group");

        producer.setNamesrvAddr("10.0.25.169:9876");
        Message message = new Message();
        message.setBody("test".getBytes(StandardCharsets.UTF_8));
        message.setTopic("test-topic");
        message.setTags("t-tags");

        try {
            producer.start();
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("send result:{}",sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    log.error("send error:",e);

                }
            });
        } catch (Exception e) {
            log.error("send error",e);
        }
        producer.shutdown();
    }


}
