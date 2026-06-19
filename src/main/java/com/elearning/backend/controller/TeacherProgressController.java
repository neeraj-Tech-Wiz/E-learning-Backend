package com.elearning.backend.controller;

import com.elearning.backend.dto.StudentProgressSummaryDTO;
import com.elearning.backend.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/progress")
@RequiredArgsConstructor
public class TeacherProgressController {

    private final ProgressService progressService;

    //  Get progress of ALL students
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<StudentProgressSummaryDTO>> getAllProgress() {
        return ResponseEntity.ok(progressService.getAllStudentsProgress());
    }

    // Search students by name
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<StudentProgressSummaryDTO>> searchProgress(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(progressService.searchStudentProgress(name));
    }

    // Get progress of ONE student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<StudentProgressSummaryDTO> getSingleStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(progressService.getStudentProgressSummary(studentId));
    }
}
