package ru.cotp.mock.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;



@Configuration
@EnableKafka
@EnableCaching
public class AppConfig {

    @Value(value = "${spring.kafka.topic.name}")
    private String topicName;

    @Bean
    public NewTopic topic(){
        return TopicBuilder.name(topicName).build();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("addresses");
    }
}