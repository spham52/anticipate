package com.example.smsserver.service;

import com.example.smsserver.dto.SensorNotificationDTO;
import com.example.smsserver.dto.SensorRegistrationRequestDTO;
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

    @Override
    public List<SensorNotification> findAllNotificationsBySensor(Sensor sensor, String userID) {
        if (!sensor.getUser().getUserID().equals(userID)) {
            throw new UnauthorisedAccessException("You do not own this sensor");
        }

        // add token verification
        return sensorNotificationRepository.
                findSensorNotificationBySensorIdOrderByTimestampDesc(sensor.getId());
    }

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
}
