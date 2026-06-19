package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyAttendanceAnalyticsDTO {
    private int month;
    private long presentCount;
}
