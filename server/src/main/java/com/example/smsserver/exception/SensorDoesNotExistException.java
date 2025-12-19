package com.example.smsserver.exception;

import lombok.Getter;

@Getter
public class SensorDoesNotExistException extends RuntimeException {
  private final String sensorID;

    public SensorDoesNotExistException(String sensorID) {
        super("Sensor ID: " + sensorID + " does not exist");
        this.sensorID = sensorID;
    }
}
