package com.escuelgaing.edu.co;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.escuelgaing.edu.co.repository")
public class BlackJackRoyaleApp {
    public static void main(String[] args) {
        SpringApplication.run(BlackJackRoyaleApp.class, args);
    }
}
