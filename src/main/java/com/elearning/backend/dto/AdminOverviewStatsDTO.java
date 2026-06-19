package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOverviewStatsDTO {
    private long totalStudents;
    private long totalTeachers;
    private long totalUsers;
}
