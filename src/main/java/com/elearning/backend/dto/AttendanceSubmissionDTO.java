package com.elearning.backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSubmissionDTO {

    private LocalDate date;

    private List<StudentStatus> statuses;

    // --- Nested Class for Student Status ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStatus {
        private Long studentId;
        private boolean present;
    }
}