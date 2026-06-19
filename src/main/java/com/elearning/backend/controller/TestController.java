package com.elearning.backend.controller;
import com.elearning.backend.dto.StudentTestOverviewDTO;
import com.elearning.backend.dto.TestDetailDTO;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.Test;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TestRepository;
import com.elearning.backend.service.TestMapper;
import com.elearning.backend.service.TestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "http://localhost:3000") // allow frontend later
public class TestController {
    @Autowired

    private final TestRepository testRepository;
    private final TestService testService;
    private final TestMapper testMapper;
    private final StudentRepository studentRepository;

    public TestController(
            TestRepository testRepository,
            TestService testService,
            TestMapper testMapper,
            StudentRepository studentRepository
    ) {
        this.testRepository = testRepository;
        this.testService = testService;
        this.testMapper = testMapper;
        this.studentRepository = studentRepository;
    }

    /* =====================================================
       STUDENT: GET TESTS (FILTERED BY STANDARD)
    ===================================================== */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<TestDetailDTO>> getTestsForStudent(Principal principal) {

        String email = principal.getName();

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Test> tests = testRepository.findByStandard(student.getStandard());

        List<TestDetailDTO> dto = tests.stream()
                .map(testMapper::toDto)
                .toList();

        return ResponseEntity.ok(dto);
    }

    /* =====================================================
       TEACHER: GET TESTS BY TEACHER ID (OPTIONAL / LEGACY)
    ===================================================== */
    @GetMapping("/teacher/{id}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public List<Test> getTestsByTeacher(@PathVariable Long id) {
        return testRepository.findByTeacherId(id);
    }

    /* =====================================================
       TEACHER: GET MY TESTS (SECURE & PREFERRED)
    ===================================================== */
    @GetMapping("/teacher/my")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<TestDetailDTO>> getMyTests(Principal principal) {

        String teacherEmail = principal.getName();

        List<Test> tests = testService.getTestsByTeacherEmail(teacherEmail);

        List<TestDetailDTO> dto = tests.stream()
                .map(testMapper::toDto)
                .toList();

        return ResponseEntity.ok(dto);
    }
    @GetMapping("/student/my")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<StudentTestOverviewDTO>> getStudentTests(Principal principal) {
        return ResponseEntity.ok(
                testService.getStudentTests(principal.getName())
        );
    }


    /* =====================================================
       TEACHER: CREATE TEST
    ===================================================== */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<TestDetailDTO> createTest(
            @Valid
            @RequestBody Test test,
            Principal principal
    ) {

        Test saved = testService.createTestWithQuestions(test, principal.getName());

        TestDetailDTO dto = testMapper.toDto(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}

