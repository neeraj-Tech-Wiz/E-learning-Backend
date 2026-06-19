package com.elearning.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
    public class StudentTestOverviewDTO {
        private Long testId;
        private String title;
        private LocalDate date;
        private Integer durationMinutes;
        private boolean reviewEnabled;

        private boolean attempted;
        private Integer score;
        private Integer totalQuestions;
    }