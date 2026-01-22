package com.example.smsserver.controller;

import com.example.smsserver.dto.Sensor.SensorHistoryDTO;
import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.example.smsserver.dto.Sensor.SensorRegistrationRequestDTO;
import com.example.smsserver.dto.Sensor.SensorResponseDTO;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.User;
import com.example.smsserver.service.SensorService;
import com.example.smsserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<SensorHistoryDTO>> getNotificationHistoryBySensorID(
            @PathVariable String sensorID,
            @AuthenticationPrincipal String userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SensorHistoryDTO> sensorNotifications = sensorService.findAllNotificationsBySensorPageable(page,
                size, sensorID, userID);
        return new ResponseEntity<>(sensorNotifications, HttpStatus.OK);
    }

    // get all sensors owned by a user
    @GetMapping
    public ResponseEntity<List<SensorResponseDTO>> getAllSensorsByUser(@AuthenticationPrincipal String userID) {
        List<SensorResponseDTO> sensors = sensorService.findSensorsDTOByUser(userID);
        return new ResponseEntity<>(sensors, HttpStatus.OK);
    }
}
