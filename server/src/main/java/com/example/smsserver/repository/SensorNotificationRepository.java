package com.example.smsserver.repository;

import com.example.smsserver.model.SensorNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorNotificationRepository extends JpaRepository<SensorNotification, Long> {

    List<SensorNotification> findSensorNotificationBySensorIdOrderByTimestampDesc(String sensorId);
}
