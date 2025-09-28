package com.example.smsserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "sensors")

// This is an Entity class which represents the Pico Device
public class Sensor {

    @Id
    @NotBlank
    @NotNull
    @Column(unique = true, nullable = false)
    private String id;
}
