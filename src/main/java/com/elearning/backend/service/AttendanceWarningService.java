package com.elearning.backend.service;

import com.elearning.backend.dto.AttendanceWarningRequestDTO;
import com.elearning.backend.model.Student;
import com.elearning.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceWarningService {

    private final StudentRepository studentRepository;
    private final EmailService emailService;

    public void sendAttendanceWarning(AttendanceWarningRequestDTO request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Fire email
        emailService.sendWarningEmail(
                student.getEmail(),
                student.getName(),
                request.getAttendancePercentage(),
                request.getMonth(),
                request.getYear()
        );
    }
}
