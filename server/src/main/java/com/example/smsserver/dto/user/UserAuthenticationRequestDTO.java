package com.example.smsserver.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// DTO when user sends login request to server
public class UserAuthenticationRequestDTO {
    private String email;
    private String password;
}
