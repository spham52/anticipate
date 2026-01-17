package com.example.smsserver.service;

import com.example.smsserver.dto.Sensor.SensorHistoryDTO;
import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.example.smsserver.dto.Sensor.SensorRegistrationRequestDTO;
import com.example.smsserver.dto.Sensor.SensorResponseDTO;
import com.example.smsserver.exception.SensorAlreadyAssociatedWithUserException;
import com.example.smsserver.exception.SensorDoesNotExistException;
import com.example.smsserver.exception.UnauthorisedAccessException;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.SensorNotification;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.SensorNotificationRepository;
import com.example.smsserver.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;
    private final SensorNotificationRepository sensorNotificationRepository;
    private final UserService userService;

    @Override
    public void associateUserWithSensor(SensorRegistrationRequestDTO request, String userID) {
        Sensor sensor = findSensorById(request.getSensorID());

        // if user already associated with sensor, throw exception
        if (sensor.getUser() != null) {
            throw new SensorAlreadyAssociatedWithUserException(sensor.getId());
        }

        // find user associated with userID in request
        User user = userService.findUserById(userID);
        sensor.setUser(user);
        sensorRepository.save(sensor);
    }

    // find all notifications associated with a specific sensor
    @Override
    public List<SensorNotification> findAllNotificationsBySensor(Sensor sensor, String userID) {
        if (!sensor.getUser().getUserID().equals(userID)) {
            throw new UnauthorisedAccessException("You do not own this sensor");
        }

        // add token verification
        return sensorNotificationRepository.
                findSensorNotificationBySensorIdOrderByTimestampDesc(sensor.getId());
    }

    // save notification received from a sensor into DB
    @Override
    public void saveNotification(SensorNotificationDTO notification) {
        LocalDateTime time = LocalDateTime.now();
        Sensor sensor = findSensorById(notification.getSensorID());
        SensorNotification sensorNotification = SensorNotification.builder()
                .sensor(sensor)
                .timestamp(time)
                .build();
        sensorNotificationRepository.save(sensorNotification);
    }

    @Override
    public Sensor findSensorById(String sensorId) {
        return sensorRepository.findById(sensorId).orElseThrow(() ->
                new SensorDoesNotExistException(sensorId));
    }

    @Override
    // find all sensors owned by user
    public List<Sensor> findSensorsByUser(User user) {
        return sensorRepository.findSensorByUser(user);
    }

    public List<SensorResponseDTO> findSensorsDTOByUser(User user) {
        return findSensorsByUser(user).stream()
                .map(sensor -> new SensorResponseDTO(sensor.getId()))
                .toList();
    }

    public List<SensorHistoryDTO> findAllNotificationsDTOBySensor(Sensor sensor, String userID) {
        return findAllNotificationsBySensor(sensor, userID).stream()
                .map(s -> new SensorHistoryDTO(s.getId(),
                        s.getSensor().getId(),
                        s.getTimestamp()))
                .toList();
    }
}
