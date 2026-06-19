package com.elearning.backend.controller;

import com.elearning.backend.dto.AdminOverviewStatsDTO;
import com.elearning.backend.dto.MonthlyAttendanceAnalyticsDTO;
import com.elearning.backend.dto.TeacherAttendanceHeatmapDTO;
import com.elearning.backend.dto.UsersDistributionDTO;
import com.elearning.backend.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;

    @GetMapping("/overview")
    public AdminOverviewStatsDTO overview() {
        return analyticsService.getOverviewStats();
    }

    @GetMapping("/users-distribution")
    public List<UsersDistributionDTO> usersDistribution() {
        return analyticsService.getUsersDistribution();
    }

    @GetMapping("/attendance/monthly")
    public List<MonthlyAttendanceAnalyticsDTO> monthlyAttendance(
            @RequestParam int year
    ) {
        return analyticsService.getMonthlyAttendance(year);
    }

    @GetMapping("/attendance/heatmap")
    public List<TeacherAttendanceHeatmapDTO> teacherAttendanceHeatmap(
            @RequestParam int year
    ) {
        return analyticsService.getTeacherAttendanceHeatmap(year);
    }
}
