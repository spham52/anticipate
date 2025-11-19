package com.example.smsserver.controller;

import com.example.smsserver.dto.SensorRegistrationRequest;
import com.example.smsserver.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
// controller associating users with sensor
public class SensorController {
    private final SensorService sensorService;

    // associate user with sensor
    // for now, saving sensors into DB will be done manually
    @PostMapping("/sensor/register")
    public ResponseEntity<String> registerSensor(@RequestBody @Valid SensorRegistrationRequest request) {
        sensorService.associateUserWithSensor(request);
        return ResponseEntity.ok("Sensor associated with user successfully");
    }
}
