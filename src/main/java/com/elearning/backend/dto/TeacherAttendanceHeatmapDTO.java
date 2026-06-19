package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherAttendanceHeatmapDTO {
    private Long teacherId;
    private String teacherName;
    private int month;
    private long attendanceCount;
}
