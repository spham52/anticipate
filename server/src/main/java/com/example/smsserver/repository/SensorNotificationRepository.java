package com.example.smsserver.repository;

import com.example.smsserver.model.SensorNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SensorNotificationRepository extends JpaRepository<SensorNotification, Long> {

    List<SensorNotification> findSensorNotificationBySensorIdOrderByTimestampDesc(String sensorId);

    Page<SensorNotification> findSensorNotificationBySensorIdOrderByTimestampDesc(String sensorId, Pageable pageable);

    List<SensorNotification> findSensorNotificationBySensorIdAndTimestampBetweenOrderByTimestampDesc(String sensorId,
                                                                                                     Instant from,
                                                                                                     Instant to);
}
