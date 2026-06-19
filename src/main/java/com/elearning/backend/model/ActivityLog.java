package com.elearning.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorEmail;
    private String actorRole;

    private String action;
    private String entityType; // STUDENT / TEACHER / ATTENDANCE
    private String entityId;

    @Column(length = 1000)
    private String description;

    private LocalDateTime timestamp;

    private String ipAddress;
}
