package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentCurrentAttendanceDto {

    private int month;
    private int year;
    private int daysPresent;
    private int totalDays;
    private int percentage;
}
