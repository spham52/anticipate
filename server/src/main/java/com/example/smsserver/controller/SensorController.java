package com.example.smsserver.controller;

import com.example.smsserver.dto.SensorRegistrationRequestDTO;
import com.example.smsserver.dto.SensorResponseDTO;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.SensorNotification;
import com.example.smsserver.model.User;
import com.example.smsserver.service.SensorService;
import com.example.smsserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/sensor")
@RequiredArgsConstructor
// controller associating users with sensor
public class SensorController {
    private final SensorService sensorService;
    private final UserService userService;

    // associate user with sensor
    // for now, saving sensors into DB will be done manually
    @PostMapping("/register")
    public ResponseEntity<String> registerSensor(@RequestBody @Valid SensorRegistrationRequestDTO request,
                                                 @AuthenticationPrincipal String userID) {
        sensorService.associateUserWithSensor(request, userID);
        return ResponseEntity.ok("Sensor associated with user successfully");
    }

    // get notification history from a specific sensor
    @GetMapping("/{sensorID}/history")
    public ResponseEntity<String> getNotificationHistoryBySensorID(@PathVariable String sensorID,
                                                                   @AuthenticationPrincipal String userID) {
        Sensor sensor = sensorService.findSensorById(sensorID);
        List<SensorNotification> sensorNotifications = sensorService.findAllNotificationsBySensor(sensor, userID);
        return new ResponseEntity<>(sensorNotifications.toString(), HttpStatus.OK);
    }

    // get all sensors owned by a user
    @GetMapping
    public ResponseEntity<List<SensorResponseDTO>> getAllSensorsByUser(@AuthenticationPrincipal String userID) {
        User user = userService.findUserById(userID);
        List<SensorResponseDTO> sensors = sensorService.findSensorsDTOByUser(user);
        return new ResponseEntity<>(sensors, HttpStatus.OK);
    }
}
