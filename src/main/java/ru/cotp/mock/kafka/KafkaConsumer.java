package ru.cotp.mock.kafka;


import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.cotp.mock.cache.CachService;
import ru.cotp.mock.cache.MyRecord;
import ru.cotp.mock.entity.ProducerEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KafkaConsumer {
    private final CachService cashService;
    private final MeterRegistry meterRegistry;
    private Counter markCounter;


    public KafkaConsumer(CachService cashService, MeterRegistry meterRegistry) {
        this.cashService = cashService;
        this.meterRegistry = meterRegistry;
        initOrderCounters();
    }

    private void initOrderCounters() {
        markCounter = Counter.builder("mark.count")
                .tag("topic", "exemple")
                .description("The number of consumed messages")
                .register(meterRegistry);
    }



    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ProducerEntity producerEntity) {
        log.info(String.format("Json message consumed -> %s", producerEntity));
        MyRecord record = cashService.getOrCreateRecord(producerEntity.getRqId());
        try {
            CompletableFuture.runAsync(() -> sendMetricPoint("topic", producerEntity.getStatus(), calcDiff(record.creationTime())));
            cashService.deleteRecord(record.id());
            markCounter.increment();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }


    @SneakyThrows
    public void sendMetricPoint(String topic, String status, long processingTime) {
        Long timeStamp = System.currentTimeMillis();
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "admin", "123456");
        influxDB.setDatabase("cotp_mark_up");
        Point point = Point.measurement("mark_up")
                .time(timeStamp, TimeUnit.MILLISECONDS)
                .tag("topic", topic)
                .tag("status", status)
                .addField("count", 1)
                .addField("Value", processingTime)
                .build();
        influxDB.write(point);
        influxDB.close();
        log.info("Point was send");
    }

    public long calcDiff(LocalDateTime startTime) {
        return ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
    }
}