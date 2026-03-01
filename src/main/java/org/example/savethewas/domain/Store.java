package org.example.savethewas.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Store {
    private Long id;
    private String name;
    private StoreStatus status;
    private String region;
    private LocalDateTime createdAt;
}