package com.example.smsserver.config;

import org.springframework.core.io.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "firebase")
@Component
@Getter
@Setter
public class FirebaseProperties {
    private Resource serviceAccount;
}
