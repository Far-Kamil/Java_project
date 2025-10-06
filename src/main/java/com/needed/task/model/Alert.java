package com.needed.task.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.needed.task.enums.EventType;
import com.needed.task.enums.StatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Bus bus;
    @NotNull(message = "Type of incident is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @NotBlank(message = "Location could not be empty")
    private String location;
    @NotBlank(message = "Description could not be empty")
    private String description;
    @Enumerated(EnumType.STRING)
    private StatusType status;
    private List <String> photoUrls;
    private User assignedTo;

}


