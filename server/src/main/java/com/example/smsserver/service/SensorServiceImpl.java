package com.example.smsserver.service;

import com.example.smsserver.dto.SensorRegistrationRequest;
import com.example.smsserver.exception.SensorAlreadyAssociatedWithUserException;
import com.example.smsserver.exception.SensorDoesNotExistException;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;
    private final UserService userService;

    @Override
    public void associateUserWithSensor(SensorRegistrationRequest request) {
        // find sensor, throw exception if not found
        Sensor sensor = sensorRepository.findById(request.getSensorID()).orElseThrow(() ->
                new SensorDoesNotExistException(request.getSensorID()));

        // if user already associated with sensor, throw exception
        if (sensor.getUser() != null) {
            throw new SensorAlreadyAssociatedWithUserException(sensor.getId());
        }

        // find user associated with userID in request
        User user = userService.getUserById(request.getUserID());
        sensor.setUser(user);
        sensorRepository.save(sensor);
    }
}
