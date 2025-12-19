package com.example.smsserver.repository;

import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    List<Sensor> findSensorByUser(User user);
}
