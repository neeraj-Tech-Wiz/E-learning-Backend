package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.service.StudentService;
import com.elearning.backend.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;

    // =========================================================================
    // API 1: GET /api/admin/stats (Analytics Dashboard)
    // =========================================================================
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();

        AdminStatsDto stats = new AdminStatsDto(totalStudents, totalTeachers);
        return ResponseEntity.ok(stats);
    }

    // TEACHERS

    @GetMapping("/teachers")
    public ResponseEntity<PageResponse<TeacherAdminDto>> getTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(teacherService.getTeachers(page, size, search));
    }

    @PostMapping("/teachers")
    public ResponseEntity<TeacherAdminDto> createTeacher(
            @Valid @RequestBody CreateTeacherRequest request
    ) {
        return ResponseEntity.ok(teacherService.createTeacher(request));
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<TeacherAdminDto> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeacherRequest request
    ) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, request));
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/teachers/{id}/reset-password")
    public ResponseEntity<Void> resetTeacherPassword(
            @PathVariable Long id,
            @RequestBody String newPassword
    ) {
        teacherService.resetTeacherPassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    // STUDENTS

    @GetMapping("/students")
    public ResponseEntity<PageResponse<StudentAdminDto>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(studentService.getStudents(page, size, search));
    }

    @PostMapping("/students")
    public ResponseEntity<StudentAdminDto> createStudent(
            @Valid @RequestBody CreateStudentRequest request
    ) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentAdminDto> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request
    ) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/students/{id}/reset-password")
    public ResponseEntity<Void> resetStudentPassword(
            @PathVariable Long id,
            @RequestBody String newPassword
    ) {
        studentService.resetStudentPassword(id, newPassword);
        return ResponseEntity.ok().build();
    }
}
