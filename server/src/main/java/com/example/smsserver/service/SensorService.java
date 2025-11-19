package com.example.smsserver.service;

import com.example.smsserver.dto.SensorRegistrationRequest;

public interface SensorService {

    void associateUserWithSensor(SensorRegistrationRequest request);
}
