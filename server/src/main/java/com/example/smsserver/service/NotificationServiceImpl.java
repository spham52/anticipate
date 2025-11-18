package com.example.smsserver.service;

import com.example.smsserver.dto.SensorNotification;
import com.example.smsserver.model.RegistrationToken;
import com.example.smsserver.repository.TokenRegistrationRepository;
import com.example.smsserver.repository.UserSensorRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final TokenRegistrationRepository tokenRegistrationRepository;
    private final UserSensorRepository userSensorRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    // function to send notification to user associated with sensor
    public void sendNotification(SensorNotification notification) throws FirebaseMessagingException {
        String sensorID = notification.getSensorID();

        String userID = userSensorRepository.findById(sensorID)
                .orElseThrow(() -> new IllegalArgumentException("Unknown sensorID: " + sensorID))
                .getUserID();

        RegistrationToken registrationToken = tokenRegistrationRepository.findByUserID(userID);
        Message message = Message.builder()
                .setToken(registrationToken.getTokenID())
                .putData("notification", "notification")
                .build();

        firebaseMessaging.send(message);
    }
}
