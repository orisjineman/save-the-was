package org.example.savethewas.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Menu {
    private Long id;
    private Long storeId;
    private String name;
    private Boolean deleted;
    private LocalDate createdAt;
}