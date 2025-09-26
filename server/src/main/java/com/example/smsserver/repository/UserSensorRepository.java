package com.example.smsserver.repository;

import com.example.smsserver.model.UserSensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSensorRepository extends JpaRepository<UserSensor, String> {
}
