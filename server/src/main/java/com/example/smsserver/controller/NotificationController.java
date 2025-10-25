package com.example.smsserver.controller;

import com.example.smsserver.dto.SensorNotification;
import com.example.smsserver.repository.RegistrationTokenRepository;
import com.example.smsserver.repository.UserRepository;
import com.example.smsserver.repository.UserSensorRepository;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// NotificationController is used when a sensor is triggered. The PICO device will then send
// a HTTP request to the endpoint, which in turn calls a function to notify the user's device
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final RegistrationTokenRepository registrationTokenRepository;
    private final UserSensorRepository userSensorRepository;

    @PostMapping("/sensor")
    public ResponseEntity<String> sendNotification(@RequestBody SensorNotification sensorNotification) {
        String sensorID = sensorNotification.getSensorID();
        String userID = userSensorRepository.findById(sensorID).get().getUserID();
    }
}
