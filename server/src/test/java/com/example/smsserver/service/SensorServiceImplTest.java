package com.example.smsserver.service;

import com.example.smsserver.dto.SensorRegistrationRequestDTO;
import com.example.smsserver.exception.SensorAlreadyAssociatedWithUserException;
import com.example.smsserver.exception.SensorDoesNotExistException;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorServiceImplTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SensorServiceImpl sensorServiceImpl;

    @Test
    void associateUserWithSensor_throwsException_whenSensorAlreadyAssociatedWithUser() {
        String sensorID = "sensor-123";
        String userID = "user-123";

        SensorRegistrationRequestDTO sensorRegistrationRequestDTO = SensorRegistrationRequestDTO.builder()
                .sensorID(sensorID)
                .userID(userID)
                .build();

        User user = User.builder()
                .userID(userID)
                .build();

        Sensor sensor = Sensor.builder()
                .id(sensorID)
                .user(user)
                .build();

        when(sensorRepository.findById(sensorID)).thenReturn(Optional.of(sensor));

        assertThrows(SensorAlreadyAssociatedWithUserException.class,
                () -> sensorServiceImpl.associateUserWithSensor(sensorRegistrationRequestDTO, ));
    }

    @Test
    void associateUserWithSensor_throwsException_whenSensorDoesNotExist() {
        String sensorID = "sensor-123";
        String userID = "user-123";

        SensorRegistrationRequestDTO sensorRegistrationRequestDTO = SensorRegistrationRequestDTO.builder()
                .sensorID(sensorID)
                .userID(userID)
                .build();

        when(sensorRepository.findById(sensorID)).thenReturn(Optional.empty());

        assertThrows(SensorDoesNotExistException.class,
                () -> sensorServiceImpl.associateUserWithSensor(sensorRegistrationRequestDTO, userID));
    }

    @Test
    void associateUserWithSensor_whenValid() {
        String sensorID = "sensor-123";
        String userID = "user-123";

        SensorRegistrationRequestDTO sensorRegistrationRequestDTO = SensorRegistrationRequestDTO.builder()
                .sensorID(sensorID)
                .userID(userID)
                .build();

        Sensor sensor = Sensor.builder()
                .id(sensorID)
                .user(null)
                .build();

        User user = User.builder()
                .userID(userID)
                .build();

        when(sensorRepository.findById(sensorID)).thenReturn(Optional.of(sensor));
        when(userService.findUserById(userID)).thenReturn(user);

        sensorServiceImpl.associateUserWithSensor(sensorRegistrationRequestDTO, );
        assertEquals(user, sensor.getUser());
        verify(sensorRepository).save(sensor);
    }
}