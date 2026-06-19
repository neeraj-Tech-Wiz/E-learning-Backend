package com.elearning.backend.controller;

import com.elearning.backend.dto.AttendanceWarningRequestDTO;
import com.elearning.backend.service.AttendanceWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceWarningController {

    private final AttendanceWarningService warningService;

    @PostMapping("/warn")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<String> sendWarning(@RequestBody AttendanceWarningRequestDTO request) {
        warningService.sendAttendanceWarning(request);
        return ResponseEntity.ok("Warning email sent successfully");
    }
}
