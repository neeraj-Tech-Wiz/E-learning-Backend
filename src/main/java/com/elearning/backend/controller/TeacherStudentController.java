package com.elearning.backend.controller;

import com.elearning.backend.dto.StudentBasicDTO;
import com.elearning.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/students")
@RequiredArgsConstructor
public class TeacherStudentController {

    private final StudentService studentService;

    @GetMapping("/class/{standard}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<StudentBasicDTO>> getStudentsByClass(@PathVariable int standard) {
        List<StudentBasicDTO> students = studentService.getStudentsByStandard(standard);
        return ResponseEntity.ok(students);
    }
}
