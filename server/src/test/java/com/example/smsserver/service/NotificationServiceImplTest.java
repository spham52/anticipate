package com.example.smsserver.service;

import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.example.smsserver.model.TokenRegistration;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.TokenRegistrationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
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
    SensorService sensorService;

    @Mock
    FirebaseMessaging firebaseMessaging;

    @Test
    // create mock SensorNotification, UserSensor and check if
    // firebase notification is actually built correctly and sent
    void sendNotification() throws FirebaseMessagingException {
        String sensorID = "sensor-123";
        String userID = "user-123";
        String tokenID = "token-123";

        // mock SensorNotification DTO arriving into service class
        SensorNotificationDTO sensorNotificationDTO = SensorNotificationDTO.builder()
                .sensorID(sensorID).build();

        User user = new User();
        user.setUserID(userID);

        Sensor sensor = new Sensor();
        sensor.setId(sensorID);
        sensor.setUser(user);

        TokenRegistration tokenRegistration = TokenRegistration.builder()
                .tokenID(tokenID)
                .userID(userID)
                .valid(true)
                .build();

        // when SensorRepository calls findById return sensor
        when(sensorService.findSensorById(sensorID)).thenReturn(sensor);

        // when tokenRegistrationRepository calls findById return registrationToken
        when(tokenRegistrationRepository.findByUserID(userID)).thenReturn(tokenRegistration);

        // if firebaseMessaging calls send (with any Message.class) return message id
        when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id");

        notificationService.sendNotification(sensorNotificationDTO);

        // verify a messsage was actually sent
        verify(firebaseMessaging).send(any(Message.class));
        verify(sensorService).findSensorById(sensorID);
        verify(tokenRegistrationRepository).findByUserID(userID);
    }
}