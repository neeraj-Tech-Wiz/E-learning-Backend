package com.elearning.backend.service;

import com.elearning.backend.dto.AdminOverviewStatsDTO;
import com.elearning.backend.dto.MonthlyAttendanceAnalyticsDTO;
import com.elearning.backend.dto.TeacherAttendanceHeatmapDTO;
import com.elearning.backend.dto.UsersDistributionDTO;
import com.elearning.backend.repository.AttendanceRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AttendanceRepository attendanceRepository;

    // 1️⃣ Overview Cards
    public AdminOverviewStatsDTO getOverviewStats() {
        long students = studentRepository.count();
        long teachers = teacherRepository.count();

        return new AdminOverviewStatsDTO(
                students,
                teachers,
                students + teachers
        );
    }

    // 2️⃣ Users Distribution (Pie Chart)
    public List<UsersDistributionDTO> getUsersDistribution() {
        return List.of(
                new UsersDistributionDTO("STUDENTS", studentRepository.count()),
                new UsersDistributionDTO("TEACHERS", teacherRepository.count())
        );
    }

    // 3️⃣ Monthly Attendance (Line / Bar Chart)
    public List<MonthlyAttendanceAnalyticsDTO> getMonthlyAttendance(int year) {
        return attendanceRepository.getMonthlyAttendanceStats(year)
                .stream()
                .map(row -> new MonthlyAttendanceAnalyticsDTO(
                        ((Number) row[0]).intValue(),   // month
                        ((Number) row[1]).longValue()  // count
                ))
                .toList();
    }

    public List<TeacherAttendanceHeatmapDTO> getTeacherAttendanceHeatmap(int year) {
        return attendanceRepository.getTeacherAttendanceHeatmap(year)
                .stream()
                .map(row -> new TeacherAttendanceHeatmapDTO(
                        ((Number) row[0]).longValue(), // teacherId
                        (String) row[1],               // teacherName
                        ((Number) row[2]).intValue(),  // month
                        ((Number) row[3]).longValue()  // count
                ))
                .toList();
    }

}
