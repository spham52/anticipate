package com.example.smsserver.service;


import com.example.smsserver.dto.UserRegistrationRequest;
import com.example.smsserver.model.User;

public interface UserService {

    User registerUser(UserRegistrationRequest userRegistrationRequest);
}
