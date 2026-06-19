package com.elearning.backend.service;

import com.elearning.backend.dto.StudentCurrentAttendanceDto;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.repository.AttendanceRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.security.service.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class StudentAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final AuthenticatedUserService authenticatedUserService;

    // =========================================================
    // Current month attendance for logged-in student
    // =========================================================
    public StudentCurrentAttendanceDto getCurrentMonthAttendance() {

        String email = authenticatedUserService.getAuthenticatedUserEmail();

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        int totalDays = yearMonth.lengthOfMonth();

        long daysPresent = attendanceRepository.countPresentByStudentAndDateRange(
                student.getId(),
                startDate,
                endDate
        );

        int percentage = totalDays == 0
                ? 0
                : (int) Math.round((daysPresent * 100.0) / totalDays);

        return new StudentCurrentAttendanceDto(
                month,
                year,
                (int) daysPresent,
                totalDays,
                percentage
        );
    }
}
