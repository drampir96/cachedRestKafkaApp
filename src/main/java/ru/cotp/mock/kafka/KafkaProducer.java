package ru.cotp.mock.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.cotp.mock.entity.ProducerEntity;

@Slf4j
@Service
public class KafkaProducer {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    private KafkaTemplate<String, ProducerEntity> kafkaTemplate;
    public KafkaProducer(KafkaTemplate<String, ProducerEntity> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(ProducerEntity producerEntity){
        log.info(String.format("JSON message sent %s", producerEntity));

        Message<ProducerEntity> message = MessageBuilder
                .withPayload(producerEntity)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, "sample")
                .build();

        kafkaTemplate.send(message);
    }
}