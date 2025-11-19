package com.example.smsserver.exception;

import lombok.Getter;

@Getter
public class SensorAlreadyAssociatedWithUserException extends RuntimeException {
  private final String sensorID;

    public SensorAlreadyAssociatedWithUserException(String sensorID) {
        super("Sensor ID: " + sensorID + "already associated with user");
        this.sensorID = sensorID;
    }
}
