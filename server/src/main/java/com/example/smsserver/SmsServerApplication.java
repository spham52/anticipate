package com.example.smsserver;

import com.example.smsserver.model.RegistrationToken;
import com.example.smsserver.repository.RegistrationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@RequiredArgsConstructor
public class SmsServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SmsServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
