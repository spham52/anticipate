package com.example.smsserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_sensor")

// Entity class which represents the association between user and their device
public class UserSensor {

    @Id
    @NotNull
    private String sensorID;

    @NotNull
    private String userID;
}
