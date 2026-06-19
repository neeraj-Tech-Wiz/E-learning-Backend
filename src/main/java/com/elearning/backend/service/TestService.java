package com.elearning.backend.service;

import com.elearning.backend.dto.StudentTestOverviewDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.StudentTestResultRepository;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TeacherRepository teacherRepository;
    private final QuestionService questionService;
    private final StudentRepository studentRepository;
    private final StudentTestResultRepository resultRepository;

    public Test createTestWithQuestions(Test test, String teacherEmail) {

        if (test.getStandard() == null) {
            throw new IllegalArgumentException("Standard is required for a test");
        }

        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with email: " + teacherEmail));

        test.setTeacher(teacher);

        if (test.getDurationMinutes() == null || test.getDurationMinutes() <= 0) {
            test.setDurationMinutes(30);
        }

        Test savedTest = testRepository.save(test);

        if (test.getQuestions() != null) {
            for (Question question : test.getQuestions()) {
                question.setTest(savedTest);
                questionService.saveQuestion(question);
            }
        }

        return savedTest;
    }

    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }
    public List<Test> getTestsByTeacherEmail(String email) {
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return testRepository.findByTeacherId(teacher.getId());
    }
    public void save(Test test) {
        testRepository.save(test);
    }

    public List<StudentTestOverviewDTO> getStudentTests(String studentEmail) {

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Test> allTests = testRepository.findByStandard(student.getStandard());

        Map<Long, StudentTestResult> attemptedMap =
                resultRepository.findAllByStudentIdWithTest(student.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                r -> r.getTest().getId(),
                                r -> r
                        ));

        List<StudentTestOverviewDTO> response = new ArrayList<>();

        for (Test test : allTests) {
            StudentTestOverviewDTO dto = new StudentTestOverviewDTO();
            dto.setTestId(test.getId());
            dto.setTitle(test.getTitle());
            dto.setDate(test.getDate());
            dto.setDurationMinutes(test.getDurationMinutes());
            dto.setReviewEnabled(test.isReviewEnabled());

            if (attemptedMap.containsKey(test.getId())) {
                StudentTestResult r = attemptedMap.get(test.getId());
                dto.setAttempted(true);
                dto.setScore(r.getScore());
                dto.setTotalQuestions(r.getTotalQuestions());
            } else {
                dto.setAttempted(false);
            }

            response.add(dto);
        }

        return response;
    }

}
