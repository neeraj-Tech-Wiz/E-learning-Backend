package com.elearning.backend.controller;
import com.elearning.backend.dto.AttendanceSubmissionDTO;
import com.elearning.backend.dto.StudentMonthlyAttendanceDTO;
import com.elearning.backend.model.Attendance;
import  com.elearning.backend.repository.AttendanceRepository;
import com.elearning.backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor // Use Constructor Injection
@CrossOrigin(origins = "http://localhost:3000") // allow frontend later
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<Attendance>> bulkMarkAttendance(
            Principal principal,
            @RequestBody AttendanceSubmissionDTO submissionDTO) {

        // Delegate to the service, passing the secure teacher email
        List<Attendance> records = attendanceService.bulkMarkAttendance(
                principal.getName(), submissionDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(records);
    }
    @GetMapping(
            value = "/student/report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<StudentMonthlyAttendanceDTO>  getStudentReport(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month) {

        StudentMonthlyAttendanceDTO report = attendanceService.getStudentMonthlyReport(
                principal.getName(), year, month);
        return ResponseEntity.ok(report);
    }
    @GetMapping("/teacher/report")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Page<StudentMonthlyAttendanceDTO>> getTeacherReport(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StudentMonthlyAttendanceDTO> report =
                attendanceService.getTeacherDashboardReport(
                        principal.getName(), year, month, page, size);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/student/{id}")
    public List<Attendance> getByStudent(@PathVariable Long id) {
        return attendanceRepository.findByStudentId(id);
        }

    @GetMapping("/teacher/{id}")
    public List<Attendance> getByTeacher(@PathVariable Long id) {
        return attendanceRepository.findByTeacherId(id);
    }
    @GetMapping("/date/{date}")
    public List<Attendance> getAttendanceByDate(@PathVariable LocalDate date) {

        // Calls the new method in the repository to fetch attach
        return attendanceRepository.findByDate(date);
    }
}

