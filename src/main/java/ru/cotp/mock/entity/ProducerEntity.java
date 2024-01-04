package ru.cotp.mock.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProducerEntity {
    private UUID rqId;
    private String status;
}
