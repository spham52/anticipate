package com.example.smsserver.controller;

import com.example.smsserver.dto.SensorNotificationDTO;
import com.example.smsserver.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// RestController used when a sensor is triggered. The PICO device will then send
// a HTTP request to the endpoint, which in turn calls a function to notify the user's device
@RestController("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/notify")
    public ResponseEntity<String> sendNotification(@RequestBody SensorNotificationDTO sensorNotificationDTO) {
        try {
            notificationService.sendNotification(sensorNotificationDTO);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Notification sent successfully");
    }
}
