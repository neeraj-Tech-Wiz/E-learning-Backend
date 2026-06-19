package com.elearning.backend.controller;

import com.elearning.backend.dto.StudentCurrentAttendanceDto;
import com.elearning.backend.dto.StudentProfileDto;
import com.elearning.backend.dto.UpdateStudentProfileRequest;
import com.elearning.backend.service.StudentAttendanceService;
import com.elearning.backend.service.StudentProfileService;
import com.elearning.backend.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_STUDENT')")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final StudentAttendanceService studentAttendanceService;
    private final StudentService studentService;


    // GET /api/student/profile
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDto> getMyProfile() {
        return ResponseEntity.ok(studentProfileService.getMyProfile());
    }

    // PUT /api/student/profile
    @PutMapping("/profile")
    public ResponseEntity<StudentProfileDto> updateMyProfile(
            @Valid @RequestBody UpdateStudentProfileRequest request
    ) {
        return ResponseEntity.ok(studentProfileService.updateMyProfile(request));
    }
    @GetMapping("/attendance/current")
    public ResponseEntity<StudentCurrentAttendanceDto> getCurrentMonthAttendance() {
        return ResponseEntity.ok(studentAttendanceService.getCurrentMonthAttendance());
    }

    @PostMapping("/profile/photo")
    public ResponseEntity<Void> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        studentProfileService.updateProfilePhoto(file);
        return ResponseEntity.ok().build();
    }

}
