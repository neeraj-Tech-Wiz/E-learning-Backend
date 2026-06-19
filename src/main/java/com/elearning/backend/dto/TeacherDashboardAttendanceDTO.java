package com.elearning.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class TeacherDashboardAttendanceDTO {

    private int standard;
    private int month;
    private int year;
    private List<StudentMonthlyAttendanceDTO> studentReports;
}