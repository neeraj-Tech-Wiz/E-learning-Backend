// AdminStatsDto.java
package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsDto {
    private long totalStudents;
    private long totalTeachers;
}