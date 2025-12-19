package com.example.smsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// entity class which stores sensor notifications in a database
@Entity
@Data
@Table(name = "sensor_notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false, name = "timestamp")
    private LocalDateTime timestamp;


}
