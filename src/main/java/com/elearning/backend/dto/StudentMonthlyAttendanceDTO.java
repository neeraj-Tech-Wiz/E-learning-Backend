package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentMonthlyAttendanceDTO {

    private Long studentId;
    private String studentName;
    private int daysPresent;
    private int totalDaysInMonth; // e.g., 30 or 31
    private int year;

    public StudentMonthlyAttendanceDTO(Long studentId, String studentName, int daysPresent, int totalDaysInMonth, int year) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.daysPresent = daysPresent;
        this.totalDaysInMonth = totalDaysInMonth;
    }

    public StudentMonthlyAttendanceDTO(Long id, String name, int presentCount, int totalDaysInMonth, int month, int year) {
        this.studentId = id;
        this.studentName = name;
        this.daysPresent = presentCount;
        this.totalDaysInMonth = totalDaysInMonth;
        this.year = year;
    }
}