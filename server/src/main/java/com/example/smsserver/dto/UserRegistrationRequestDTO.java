package com.example.smsserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
// DTO for sending user registration information from frontend to backend
public class UserRegistrationRequestDTO {

    @NotBlank
    @NotNull
    @Size(min = 8, max = 20)
    String username;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 36)
    String password;

    @NotBlank
    @NotNull
    @Email
    String email;
}
