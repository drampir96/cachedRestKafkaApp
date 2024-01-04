package ru.cotp.mock.cache;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class CachService {
    @CachePut(cacheNames = {"addresses"}, key = "#recordId")
    public MyRecord putOrUpdate(UUID recordId) {
        return new MyRecord(recordId, LocalDateTime.now());
    }

    @CacheEvict(cacheNames = {"addresses"}, key = "#recordId")
    public void deleteRecord(UUID recordId) {
    }

    @SneakyThrows
    @Cacheable(cacheNames = {"addresses"}, key = "#recordId")
    public MyRecord getOrCreateRecord(UUID recordId) {
        log.info("submit record >>>>>");
        return new MyRecord(recordId, LocalDateTime.now());
    }

    @Cacheable(cacheNames = {"addresses"}, key = "#recordId")
    public MyRecord getRecord(UUID recordId) {
        return null;
    }
}
