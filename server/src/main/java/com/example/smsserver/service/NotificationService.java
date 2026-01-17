package com.example.smsserver.service;

import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface NotificationService {

    void sendNotification(SensorNotificationDTO notification) throws FirebaseMessagingException;
}
