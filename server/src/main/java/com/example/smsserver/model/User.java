package com.example.smsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name= "users" )

// Entity class that represents the application user
public class User {

    // userID is generated from Firebase Admin
    @Id
    private String userID;

    @Column(unique=true, nullable = false)
    @NotBlank
    @NotNull
    @Size(min = 8, max = 24)
    private String username;

    @Column(unique = true, nullable = false)
    @NotBlank
    @NotNull
    @Email
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Sensor> sensors;

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
