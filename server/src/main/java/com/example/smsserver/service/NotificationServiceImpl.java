package com.example.smsserver.service;

import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.example.smsserver.model.TokenRegistration;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.repository.TokenRegistrationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final TokenRegistrationRepository tokenRegistrationRepository;
    private final SensorService sensorService;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    // function to send notification to user associated with sensor
    public void sendNotification(SensorNotificationDTO notification) throws FirebaseMessagingException {
        String sensorID = notification.getSensorID();

        Sensor sensor = sensorService.findSensorById(sensorID);

        // if sensor is not associated with user
        if (sensor.getUser() == null) {
            throw new RuntimeException("Sensor " + sensorID + " does not have a user");
        }

        String userID = sensor.getUser().getUserID();

        // find fcm token associated with user (to send notification to specific device)
        TokenRegistration tokenRegistration = tokenRegistrationRepository.findByUserID(userID);

        if (tokenRegistration == null) {
            throw new RuntimeException("Registration token for user " + userID + " not found");
        }

        Message message = Message.builder()
                .setToken(tokenRegistration.getTokenID())
                .setNotification(Notification.builder()
                        .setTitle("Motion detected")
                        .setBody(sensorID + " has detected movement at " + Instant.now())
                        .build())
                .build();

        // save notification history into db
        sensorService.saveNotification(notification);

        // send notification to user's device
        firebaseMessaging.send(message);
    }
}
