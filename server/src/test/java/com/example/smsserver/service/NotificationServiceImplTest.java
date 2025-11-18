package com.example.smsserver.service;

import com.example.smsserver.dto.SensorNotification;
import com.example.smsserver.model.RegistrationToken;
import com.example.smsserver.model.UserSensor;
import com.example.smsserver.repository.TokenRegistrationRepository;
import com.example.smsserver.repository.UserSensorRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test class for NotificationService
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @InjectMocks
    NotificationServiceImpl notificationService;

    @Mock
    TokenRegistrationRepository tokenRegistrationRepository;

    @Mock
    UserSensorRepository userSensorRepository;

    @Mock
    FirebaseMessaging firebaseMessaging;

    @Test
    // create mock SensorNotification, UserSensor and check if
    // firebase notification is actually built correctly and sent
    void sendNotification() throws FirebaseMessagingException {
        String sensorID = "sensor-123";
        String userID = "user-123";
        String tokenID = "token-123";

        // mock SensorNotification POJO
        SensorNotification sensorNotification = SensorNotification.builder()
                .sensorID(sensorID).build();

        // mock UserSensor entity class
        UserSensor userSensor = UserSensor.builder()
                .sensorID(sensorID)
                .userID(userID)
                .build();

        RegistrationToken registrationToken = RegistrationToken.builder()
                .tokenID(tokenID)
                .userID(userID)
                .valid(true)
                .build();

        // when userSensorRepository calls findById return userSensor
        when(userSensorRepository.findById(sensorID)).thenReturn(Optional.of(userSensor));

        // when tokenRegistrationRepository calls findById return registrationToken
        when(tokenRegistrationRepository.findByUserID(userID)).thenReturn(registrationToken);

        // if firebaseMessaging calls send (with any Message.class) return message id
        when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id");

        notificationService.sendNotification(sensorNotification);

        // verify a messsage was actually sent
        verify(firebaseMessaging).send(any(Message.class));
        verify(userSensorRepository).findById(sensorID);
        verify(tokenRegistrationRepository).findByUserID(userID);
    }
}