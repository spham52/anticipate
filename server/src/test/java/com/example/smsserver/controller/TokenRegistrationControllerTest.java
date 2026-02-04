package com.example.smsserver.controller;

import com.example.smsserver.dto.token.TokenRegistrationRequestDTO;
import com.example.smsserver.service.TokenRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenRegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class TokenRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenRegistrationService tokenRegistrationService;

    @MockitoBean
    private FirebaseAuth firebaseAuth;

    @Test
    void registerToken_shouldSaveToken() throws Exception {
        TokenRegistrationRequestDTO registrationRequest = TokenRegistrationRequestDTO.builder()
                .tokenID("123")
                .appVersion("1.0")
                .platform("android")
                .build();

        mockMvc.perform(post("/token/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        verify(tokenRegistrationService).registerToken(any(), any());
    }
}
