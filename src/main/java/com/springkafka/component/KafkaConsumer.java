//package com.springkafka.component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//@Data
//public class KafkaConsumer {
//
//    private CountDownLatch latch = new CountDownLatch(5);
//    private List<String> payloads = new ArrayList<>();
//    private String payload;
//
//    @KafkaListener(topics = "baeldung",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void receive(ConsumerRecord<String, String> consumerRecord) {
//        payload = consumerRecord.value();
//        log.info("received payload = {}", payload.toString());
//        payloads.add(payload);
//        latch.countDown();
//    }
//
//    public List<String> getPayloads() {
//        return payloads;
//    }
//
//    public void resetLatch() {
//        latch = new CountDownLatch(3);
//    }
//}
