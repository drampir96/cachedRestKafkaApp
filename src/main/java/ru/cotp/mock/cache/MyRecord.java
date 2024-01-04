package ru.cotp.mock.cache;

import java.time.LocalDateTime;
import java.util.UUID;

public record MyRecord(UUID id, LocalDateTime creationTime) {
}