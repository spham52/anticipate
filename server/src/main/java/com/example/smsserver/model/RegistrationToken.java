package com.example.smsserver.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Java POJO for "RegistrationToken"
// This is what will be saved into the token database
@Data
@Builder
@Document("RegistrationToken")
public class RegistrationToken {
    @Id
    String tokenID;
    String userID;

    @Builder.Default
    long timeCreated = System.currentTimeMillis();

    @Builder.Default
    boolean valid = true;

    String platform;
    String appVersion;
}
