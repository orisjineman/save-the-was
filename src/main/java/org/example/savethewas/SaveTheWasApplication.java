package org.example.savethewas;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.example.savethewas.mapper")
@SpringBootApplication
public class SaveTheWasApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaveTheWasApplication.class, args);
    }

}
