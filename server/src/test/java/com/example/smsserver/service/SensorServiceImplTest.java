package com.example.smsserver.service;

import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.example.smsserver.dto.Sensor.SensorRegistrationRequestDTO;
import com.example.smsserver.exception.SensorAlreadyAssociatedWithUserException;
import com.example.smsserver.exception.SensorDoesNotExistException;
import com.example.smsserver.exception.UnauthorisedAccessException;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.SensorNotification;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.SensorNotificationRepository;
import com.example.smsserver.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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

    @Mock
    SensorNotificationRepository sensorNotificationRepository;

    @InjectMocks
    private SensorServiceImpl sensorServiceImpl;

    @Test
    void associateUserWithSensor_throwsException_whenSensorAlreadyAssociatedWithUser() {
        String sensorID = "sensor-123";
        String userID = "user-123";

        SensorRegistrationRequestDTO sensorRegistrationRequestDTO = SensorRegistrationRequestDTO.builder()
                .sensorID(sensorID)
                .build();

        User user = User.builder()
                .userID(userID)
                .build();

        Sensor sensor = Sensor.builder()
                .id(sensorID)
                .user(user)
                .build();

        // mock findById
        when(sensorRepository.findById(sensorID)).thenReturn(Optional.of(sensor));

        // check if SensorAlreadyAssociatedWithUser is thrown
        assertThrows(SensorAlreadyAssociatedWithUserException.class,
                () -> sensorServiceImpl.associateUserWithSensor(sensorRegistrationRequestDTO, userID));
    }

    @Test
    void associateUserWithSensor_throwsException_whenSensorDoesNotExist() {
        String sensorID = "sensor-123";
        String userID = "user-123";

        SensorRegistrationRequestDTO sensorRegistrationRequestDTO = SensorRegistrationRequestDTO.builder()
                .sensorID(sensorID)
                .build();

        // mock findById
        when(sensorRepository.findById(sensorID)).thenReturn(Optional.empty());

        // checks if SensorDoesNotExist was thrown
        assertThrows(SensorDoesNotExistException.class,
                () -> sensorServiceImpl.associateUserWithSensor(sensorRegistrationRequestDTO, userID));
    }

    @Test
    void associateUserWithSensor_whenValid() {
        String sensorID = "sensor-123";
        String userID = "user-123";

        SensorRegistrationRequestDTO sensorRegistrationRequestDTO = SensorRegistrationRequestDTO.builder()
                .sensorID(sensorID)
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

        sensorServiceImpl.associateUserWithSensor(sensorRegistrationRequestDTO, userID);
        assertEquals(user, sensor.getUser());
        verify(sensorRepository).save(sensor);
    }


    @Test
    // test case for when user owns sensor
    void findAllNotificationsBySensor_returnsNotification_whenUserOwnsSensor() {
        User user = User.builder()
                .userID("user-123")
                .build();

        Sensor sensor = Sensor.builder()
                .user(user)
                .id("sensor-123")
                .build();

        // build sensor notification list (this is what we expect)
        List<SensorNotification> sensorNotifications = new ArrayList<>();
        SensorNotification expectedNotifications = SensorNotification.builder()
                .sensor(sensor)
                .id((long) 123)
                .build();
        sensorNotifications.add(expectedNotifications);

        // sensorRepository returns our built mock sensor object
        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensor));
        when(sensorNotificationRepository.findSensorNotificationBySensorIdOrderByTimestampDesc(sensor.getId()))
                .thenReturn(sensorNotifications);

        // call the method and check whether the function returns what we expect
        List<SensorNotification> calledNotifications = sensorServiceImpl.findAllNotificationsBySensor
                ("sensor-123", "user-123");
        assertEquals(sensorNotifications, calledNotifications);
    }


    @Test
    // checks if notification is saved when valid SensorDTO is passed
    void saveNotification_savesNotificationWithSensorAndTimestamp() {
        Sensor sensor = Sensor.builder()
                .id("sensor-123")
                .build();

        SensorNotificationDTO notificationDTO = SensorNotificationDTO.builder()
                .sensorID(sensor.getId())
                .build();

        // return built sensor when findById is called
        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensor));

        // call the tested function 'saveNotification'
        sensorServiceImpl.saveNotification(notificationDTO);

        // Create ArgumentCaptor object to capture SensorNotification class
        ArgumentCaptor<SensorNotification> captor = ArgumentCaptor.forClass(SensorNotification.class);
        // verify 'save' was called and capture the values passed into it
        verify(sensorNotificationRepository).save(captor.capture());

        SensorNotification saved = captor.getValue();

        // check that the saved sensor equals the sensor built
        assertEquals(sensor, saved.getSensor());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void findSensorById_throwsException_whenSensorDoesNotExist() {
        String sensorID = "sensor-321";
        when(sensorRepository.findById(sensorID)).thenReturn(Optional.empty());
        assertThrows(SensorDoesNotExistException.class, () -> sensorServiceImpl.findSensorById(sensorID));
    }

    @Test
    void findSensorById_returnsSensor_whenSensorExists() {
        String sensorID = "sensor-123";

        Sensor sensor = Sensor.builder()
                .id(sensorID)
                .build();

        when(sensorRepository.findById(sensorID)).thenReturn(Optional.of(sensor));
        Sensor saved = sensorServiceImpl.findSensorById(sensorID);
        assertEquals(sensor, saved);
    }

    @Test
    void findSensorsByUser_returnsSensor_whenValid() {
        String userID = "user-123";

        User user = User.builder()
                .userID(userID)
                .build();

        List<Sensor> sensors = new ArrayList<>();
        Sensor expectedSensor = Sensor.builder()
                .user(user)
                .id("sensor-123")
                .build();

        sensors.add(expectedSensor);

        // mock dependencies
        when(sensorRepository.findSensorByUser(user)).thenReturn(sensors);
        when(userService.findUserById(userID)).thenReturn(user);

        List<Sensor> calledSensor = sensorServiceImpl.findSensorsByUser(userID);

        // check if returned value matches expected value
        assertEquals(sensors, calledSensor);
    }

    @Test
    void checkSensorOwnership_throwsUnauthorisedException_whenSensorDoesNotBelongToUser() {
        User userOne = User.builder()
                .userID("user-123")
                .build();

        User userTwo = User.builder()
                .userID("user-321")
                .build();

        Sensor sensor = Sensor.builder()
                .user(userOne)
                .build();

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensor));
        assertThrows(UnauthorisedAccessException.class,
                () -> sensorServiceImpl.checkSensorOwnership(sensor.getId(), userTwo.getUserID()));
    }

    @Test
    void checkSensorOwnership_doesNotThrowUnauthorisedException_whenValid() {
        User userOne = User.builder()
                .userID("user-123")
                .build();

        Sensor sensor = Sensor.builder()
                .user(userOne)
                .build();

        when(sensorRepository.findById(sensor.getId())).thenReturn(Optional.of(sensor));
        assertDoesNotThrow(() -> sensorServiceImpl.checkSensorOwnership(sensor.getId(), userOne.getUserID()));
    }

    @Test
    // to-do
    void findAllNotificationsDTOHourAggregateByDate() {
    }
}