package com.example.smsserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("RegistrationToken")
// MongoDB document class representing a Firebase Registration Token
// each entry represents a token used for sending notifications to a specified user
public class TokenRegistration {
    @Id
    private String tokenID;

    private String userID;

    @Builder.Default
    private long timeCreated = System.currentTimeMillis();

    @Builder.Default
    private boolean valid = true;

    private String platform;
    private String appVersion;
}
