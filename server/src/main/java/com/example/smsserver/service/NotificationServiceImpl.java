package com.example.smsserver.service;

import com.example.smsserver.dto.SensorNotification;
import com.example.smsserver.model.TokenRegistration;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.repository.SensorRepository;
import com.example.smsserver.repository.TokenRegistrationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final TokenRegistrationRepository tokenRegistrationRepository;
    private final SensorRepository sensorRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    // function to send notification to user associated with sensor
    public void sendNotification(SensorNotification notification) throws FirebaseMessagingException {
        String sensorID = notification.getSensorID();

        Sensor sensor = sensorRepository.findById(sensorID)
                .orElseThrow(() -> new IllegalArgumentException("Unknown sensorID: " + sensorID));

        if (sensor.getUser() == null) {
            throw new RuntimeException("Sensor " + sensorID + " does not have a user");
        }

        String userID = sensor.getUser().getUserID();

        TokenRegistration tokenRegistration = tokenRegistrationRepository.findByUserID(userID);

        if (tokenRegistration == null) {
            throw new RuntimeException("Registration token for user " + userID + " not found");
        }

        Message message = Message.builder()
                .setToken(tokenRegistration.getTokenID())
                .putData("notification", "notification")
                .build();

        firebaseMessaging.send(message);
    }
}
