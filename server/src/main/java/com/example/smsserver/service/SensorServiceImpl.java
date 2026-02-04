package com.example.smsserver.service;

import com.example.smsserver.dto.sensor.*;
import com.example.smsserver.exception.SensorAlreadyAssociatedWithUserException;
import com.example.smsserver.exception.SensorDoesNotExistException;
import com.example.smsserver.exception.UnauthorisedAccessException;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.SensorNotification;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.SensorNotificationRepository;
import com.example.smsserver.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
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
    public List<SensorNotification> findAllNotificationsBySensor(String sensorID, String userID) {
        checkSensorOwnership(sensorID, userID);

        // add token verification
        return sensorNotificationRepository.
                findSensorNotificationBySensorIdOrderByTimestampDesc(sensorID);
    }

    // save notification received from a sensor into DB
    @Override
    public void saveNotification(SensorNotificationDTO notification) {
        Instant time = Instant.now();
        Sensor sensor = findSensorById(notification.getSensorID());
        SensorNotification sensorNotification = SensorNotification.builder()
                .sensor(sensor)
                .timestamp(time)
                .build();
        sensorNotificationRepository.save(sensorNotification);
    }

    @Override
    public Sensor findSensorById(String sensorID) {
        return sensorRepository.findById(sensorID).orElseThrow(() ->
                new SensorDoesNotExistException(sensorID));
    }

    @Override
    // find all sensors owned by user
    public List<Sensor> findSensorsByUser(String userID) {
        User user = userService.findUserById(userID);
        return sensorRepository.findSensorByUser(user);
    }

    public List<SensorResponseDTO> findSensorsDTOByUser(String userID) {
        return findSensorsByUser(userID).stream()
                .map(sensor -> new SensorResponseDTO(sensor.getId()))
                .toList();
    }

    public List<SensorHistoryDTO> findAllNotificationsDTOBySensor(String sensorID, String userID) {
        return findAllNotificationsBySensor(sensorID, userID).stream()
                .map(s -> new SensorHistoryDTO(s.getId(),
                        s.getSensor().getId(),
                        s.getTimestamp()))
                .toList();
    }

    // returns notification history of a sensor in pages
    public Page<SensorHistoryDTO> findAllNotificationsBySensorPageable(int page, int size,
                                                                       String sensorID, String userID) {
        checkSensorOwnership(sensorID, userID);
        // create pageable obj
        Pageable pageable = PageRequest.of(page, size);

        return sensorNotificationRepository.
                findSensorNotificationBySensorIdOrderByTimestampDesc(sensorID, pageable).map(s ->
                        new SensorHistoryDTO(
                                s.getId(),
                                s.getSensor().getId(),
                                s.getTimestamp()
                        ));
    }

    // check if sensor belongs to user
    public void checkSensorOwnership(String sensorID, String userID) {
        Sensor sensor = findSensorById(sensorID);

        if (!sensor.getUser().getUserID().equals(userID)) {
            throw new UnauthorisedAccessException("You do not own this sensor");
        }
    }

    // returns all notifications associated with sensor, filtered by date and grouped into hours
    public List<SensorHistoryHourAggregateDTO> findAllNotificationsDTOHourAggregateByDate(
            LocalDate date, String timezone, String sensorID, String userID) {
        checkSensorOwnership(sensorID, userID);
        ZoneId zone = ZoneId.of(timezone);

        // convert UTC from db into user's local date
        Instant from = date.atStartOfDay(zone).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zone).toInstant();

        HashMap<Integer, Integer> count = new HashMap<>();
        List<SensorNotification> notifications =
                sensorNotificationRepository.findSensorNotificationBySensorIdAndTimestampBetweenOrderByTimestampDesc(
                sensorID, from, to);

        // group into hour
        for (SensorNotification notification : notifications) {
            int hour = notification.getTimestamp().atZone(zone).getHour();
            count.put(hour, count.getOrDefault(hour, 0) + 1);
        }

        // return hashmap as DTO
        return count.entrySet().stream().map(m ->
                new SensorHistoryHourAggregateDTO(m.getKey(), m.getValue()))
                .sorted(Comparator.comparingInt(SensorHistoryHourAggregateDTO::getHour))
                .toList();
    }
}
