package com.example.smsserver.service;

import com.example.smsserver.dto.SensorNotification;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface NotificationService {

    void sendNotification(SensorNotification notification) throws FirebaseMessagingException;
}
