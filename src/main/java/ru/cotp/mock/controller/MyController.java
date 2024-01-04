package ru.cotp.mock.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.cotp.mock.entity.ProducerEntity;
import ru.cotp.mock.cache.CachService;
import ru.cotp.mock.kafka.KafkaProducer;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyController {

    @Autowired
    private KafkaProducer kafkaProducer;

    private final CachService cashService;


    @SneakyThrows
    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody ProducerEntity producerEntity) {
        cashService.getOrCreateRecord(producerEntity.getRqId());
        Thread.sleep(3000);
        kafkaProducer.sendMessage(producerEntity);
        return ResponseEntity.ok("Json message sent to kafka topic");
    }


    @PostMapping("/putCache")
    public ResponseEntity<String> validate(@RequestBody ProducerEntity producerEntity) {
        cashService.putOrUpdate(producerEntity.getRqId());
        return ResponseEntity.ok("Json message sent to kafka topic");
    }

    @PostMapping("/getCache")
    public ResponseEntity<String> getCashe(@RequestBody ProducerEntity producerEntity) {
        if (cashService.getRecord(producerEntity.getRqId()) != null) {
            log.info("getCache >>> found data in Cache");
        } else {
            log.info("getCache >>> no data in Cache");
        }
        return ResponseEntity.ok("Json message sent to kafka topic");
    }

    @PostMapping("/deleteCache")
    public ResponseEntity<String> delete(@RequestBody ProducerEntity producerEntity) {
        cashService.deleteRecord(producerEntity.getRqId());
        log.info("data deleted from Cache");
        return ResponseEntity.ok("Json message sent to kafka topic");
    }

}
