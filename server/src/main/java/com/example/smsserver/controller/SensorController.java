package com.example.smsserver.controller;

import com.example.smsserver.dto.sensor.*;
import com.example.smsserver.service.SensorService;
import com.example.smsserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
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

    @GetMapping("/{sensorID}/history/date")
    public ResponseEntity<List<SensorHistoryHourAggregateDTO>> getNotificationHistoryDailyBySensorID(
            @PathVariable String sensorID,
            @AuthenticationPrincipal String userID,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "Australia/Sydney") String timezone
    ) {
        if (date == null) Instant.now();
        List<SensorHistoryHourAggregateDTO> history = sensorService.findAllNotificationsDTOHourAggregateByDate(date, timezone,
                sensorID, userID);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
