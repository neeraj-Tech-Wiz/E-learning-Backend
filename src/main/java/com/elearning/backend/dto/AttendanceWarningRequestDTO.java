package com.elearning.backend.dto;

import lombok.Data;

@Data
public class AttendanceWarningRequestDTO {
    private Long studentId;
    private int year;
    private int month;
    private int attendancePercentage; // optional, for email template
}
