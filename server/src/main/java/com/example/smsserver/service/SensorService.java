package com.example.smsserver.service;

import com.example.smsserver.dto.SensorNotificationDTO;
import com.example.smsserver.dto.SensorRegistrationRequestDTO;
import com.example.smsserver.dto.SensorResponseDTO;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.SensorNotification;
import com.example.smsserver.model.User;

import java.util.List;

public interface SensorService {

    void associateUserWithSensor(SensorRegistrationRequestDTO request, String userID);

    List<SensorNotification> findAllNotificationsBySensor(Sensor sensor, String userID);

    void saveNotification(SensorNotificationDTO notification);

    Sensor findSensorById(String sensorId);

    List<Sensor> findSensorsByUser(User user);

    List<SensorResponseDTO> findSensorsDTOByUser(User user);
}
