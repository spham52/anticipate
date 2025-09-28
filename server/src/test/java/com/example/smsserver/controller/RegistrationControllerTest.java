package com.example.smsserver.controller;

import com.example.smsserver.dto.TokenRegistrationRequest;
import com.example.smsserver.repository.RegistrationTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenRegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void registerToken_shouldSaveToken() throws Exception {
        TokenRegistrationRequest registrationRequest = TokenRegistrationRequest.builder()
                .tokenID("123")
                .userID("123")
                .appVersion("1.0")
                .platform("android")
                .build();

        mockMvc.perform(post("/token/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());
    }
}
