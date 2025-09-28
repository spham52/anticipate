package com.example.smsserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name= "users" )

// Entity class that represents the application user
public class User {
    @Id
    @Builder.Default
    private String userID = UUID.randomUUID().toString();

    @Column(unique=true, nullable = false)
    @NotBlank
    @NotNull
    @Size(min = 8, max = 24)
    private String username;

    @Column(nullable = false)
    @NotBlank
    @NotNull
    @Size(min = 8, max = 36)
    private String password;

    @Column(unique = true, nullable = false)
    @NotBlank
    @NotNull
    @Email
    private String email;

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
