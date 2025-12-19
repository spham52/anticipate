package com.example.smsserver.service;


import com.example.smsserver.dto.UserRegistrationRequestDTO;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.User;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.List;

public interface UserService {

    User registerUser(UserRegistrationRequestDTO userRegistrationRequestDTO);

    User findUserById(String id);

    String createFirebaseUser(UserRegistrationRequestDTO userRegistrationRequestDTO) throws FirebaseAuthException;
}
